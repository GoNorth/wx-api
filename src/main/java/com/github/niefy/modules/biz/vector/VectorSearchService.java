package com.github.niefy.modules.biz.vector;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.fastjson.JSON;
import com.github.niefy.modules.sys.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 获取 DashScope API Key
     * @return API Key
     */
    private String getApiKey() {
        String apiKey = sysConfigService.getValue("DASHSCOPE_API_KEY");
        if (!StringUtils.hasText(apiKey)) {
            throw new RuntimeException("DashScope API Key 未配置，请在系统配置中添加 DASHSCOPE_API_KEY");
        }
        return apiKey;
    }

    // 内存缓存：存储 ID、描述和向量数据
    private List<Map<String, Object>> vectorCache = new ArrayList<>();

    /**
     * 项目启动或手动刷新时，将数据库向量加载到内存
     */
    @PostConstruct
    public void reloadCache() {
        // 只加载有效的向量数据（不为 NULL 且不为空字符串，且未删除）
        String sql = "SELECT template_id, template_image_desc, poster_type, embedding_data FROM biz_image_template " +
                "WHERE embedding_data IS NOT NULL AND embedding_data != '' AND deleted = 0";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        // 将 JSON 字符串解析为 List<Double> 提高搜索速度
        rows.forEach(row -> {
            String json = (String) row.get("embedding_data");
            if (json != null && !json.isEmpty()) {
                row.put("vector_list", JSON.parseArray(json, Double.class));
            }
        });
        this.vectorCache = rows;
        System.out.println(">>> 向量缓存加载完毕，共 " + vectorCache.size() + " 条记录");
    }

    /**
     * 核心搜索逻辑
     * @param keyword 搜索关键词
     * @param topK 返回前K个结果
     * @param posterType 海报类型过滤（可选，为null时不过滤）
     * @return 搜索结果列表
     */
    public List<Map<String, Object>> search(String keyword, int topK, String posterType) throws Exception {
        if (vectorCache == null || vectorCache.isEmpty()) {
            throw new RuntimeException("向量缓存为空，请先执行 /api/vector/init 生成向量数据");
        }

        // 1. 获取搜索词的向量
        TextEmbedding embedding = new TextEmbedding();
        TextEmbeddingParam param = TextEmbeddingParam.builder()
                .apiKey(getApiKey())
                .model("text-embedding-v3")
                .texts(Collections.singletonList(keyword))
                .build();
        TextEmbeddingResult result = embedding.call(param);
        List<Double> queryVector = result.getOutput().getEmbeddings().get(0).getEmbedding();

        // 2. 内存比对计算 (使用并行流加速)
        return vectorCache.parallelStream()
                .filter(item -> item.get("vector_list") != null) // 过滤掉向量为空的记录
                .filter(item -> {
                    // 如果指定了posterType，则进行过滤
                    if (StringUtils.hasText(posterType)) {
                        Object itemPosterType = item.get("poster_type");
                        // 处理null值情况：如果数据库中的poster_type为null，则不匹配
                        return itemPosterType != null && posterType.equals(itemPosterType.toString());
                    }
                    return true;
                })
                .map(item -> {
                    List<Double> itemVector = (List<Double>) item.get("vector_list");
                    double similarity = calculateCosineSimilarity(queryVector, itemVector);

                    // 构建返回结果，不包含原始大向量以节省带宽
                    Map<String, Object> res = new HashMap<>();
                    res.put("template_id", item.get("template_id"));
                    res.put("desc", item.get("template_image_desc"));
                    res.put("poster_type", item.get("poster_type"));
                    res.put("score", similarity);
                    return res;
                })
                .filter(item -> (double)item.get("score") > 0.3) // 过滤掉完全不相关的
                .sorted((a, b) -> Double.compare((double) b.get("score"), (double) a.get("score")))
                .limit(topK)
                .collect(Collectors.toList());
    }

    /**
     * 余弦相似度算法
     */
    private double calculateCosineSimilarity(List<Double> v1, List<Double> v2) {
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}