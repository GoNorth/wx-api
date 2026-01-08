package com.github.niefy.modules.biz.vector;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.fastjson.JSON;
import com.github.niefy.modules.sys.service.SysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VectorSearchService {

    private static final Logger logger = LoggerFactory.getLogger(VectorSearchService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 是否在应用启动时自动加载向量缓存
     * 可通过 application.yml 中的 vector.auto-reload-cache 配置
     */
    @Value("${vector.auto-reload-cache:true}")
    private boolean autoReloadCache;

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
     * 项目启动时，根据配置决定是否自动加载向量缓存
     */
    @PostConstruct
    public void init() {
        if (autoReloadCache) {
            reloadCache();
        } else {
            logger.warn("========================================");
            logger.warn(">>> 向量缓存自动加载已禁用");
            logger.warn(">>> 可通过 /api/vector/refresh 手动刷新");
            logger.warn("========================================");
        }
    }

    /**
     * 手动刷新或启动时自动刷新，将数据库向量加载到内存
     */
    public void reloadCache() {
        // 只加载有效的向量数据（不为 NULL 且不为空字符串，且未删除，且状态为 PUBLISH）
        // 查询包含所有业务字段，以便在搜索结果中返回完整信息
        String sql = "SELECT template_id, dish_category, price_display, product_type, template_image_desc, tags, poster_type, status, embedding_data " +
                "FROM biz_image_template " +
                "WHERE embedding_data IS NOT NULL AND embedding_data != '' AND deleted = 0 AND status = 'PUBLISH'";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        // 将 JSON 字符串解析为 List<Double> 提高搜索速度
        rows.forEach(row -> {
            String json = (String) row.get("embedding_data");
            if (json != null && !json.isEmpty()) {
                row.put("vector_list", JSON.parseArray(json, Double.class));
            }
        });
        this.vectorCache = rows;
        logger.info("========================================");
        logger.info(">>> 向量缓存加载完毕，共 {} 条记录", vectorCache.size());
        logger.info("========================================");
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

        // 优先使用传统字段查询：posterType 和 tags
        List<Map<String, Object>> traditionalResults = searchByTraditionalFields(keyword, topK, posterType);
        
        // 如果传统查询有结果，直接返回
        if (traditionalResults != null && !traditionalResults.isEmpty()) {
            logger.info("使用传统字段查询，找到 {} 条结果", traditionalResults.size());
            return traditionalResults;
        }

        // 传统查询无结果，使用向量匹配
        logger.info("传统字段查询无结果，使用向量匹配");
        return searchByVector(keyword, topK, posterType);
    }

    /**
     * 传统字段查询：posterType 和 tags
     * @param keyword 搜索关键词
     * @param topK 返回前K个结果
     * @param posterType 海报类型过滤（可选，为null时不过滤）
     * @return 搜索结果列表
     */
    private List<Map<String, Object>> searchByTraditionalFields(String keyword, int topK, String posterType) {
        // 构建 SQL 查询条件
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT template_id, dish_category, price_display, product_type, template_image_desc, tags, poster_type ");
        sql.append("FROM biz_image_template ");
        sql.append("WHERE deleted = 0 AND status = 'PUBLISH' ");
        
        List<Object> params = new ArrayList<>();
        
        // 如果指定了 posterType，添加条件
        if (StringUtils.hasText(posterType)) {
            sql.append("AND poster_type = ? ");
            params.add(posterType);
        }
        
        // 如果指定了 keyword，在 tags 字段中搜索
        if (StringUtils.hasText(keyword)) {
            sql.append("AND tags LIKE ? ");
            params.add("%" + keyword + "%");
        }
        
        sql.append("LIMIT ?");
        params.add(topK);
        
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            
            // 构建返回结果，设置 score 为 1.0（表示完全匹配）
            return results.stream().map(row -> {
                Map<String, Object> res = new HashMap<>();
                res.put("template_id", row.get("template_id"));
                res.put("desc", row.get("template_image_desc"));
                res.put("poster_type", row.get("poster_type"));
                res.put("score", 1.0); // 传统查询完全匹配，score 设为 1.0
                return res;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("传统字段查询失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 向量匹配查询
     * @param keyword 搜索关键词
     * @param topK 返回前K个结果
     * @param posterType 海报类型过滤（可选，为null时不过滤）
     * @return 搜索结果列表
     */
    private List<Map<String, Object>> searchByVector(String keyword, int topK, String posterType) throws Exception {
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
                    // 只返回状态为 PUBLISH 的记录
                    Object itemStatus = item.get("status");
                    if (itemStatus == null || !"PUBLISH".equals(itemStatus.toString())) {
                        return false;
                    }
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