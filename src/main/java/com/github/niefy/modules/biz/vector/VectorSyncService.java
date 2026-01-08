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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VectorSyncService {

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

    /**
     * 初始化未向量化的数据
     * @param limit 每次处理的记录数限制
     * @return 成功处理的记录数
     */
    public int syncEmbeddings(int limit) throws Exception {
        // 1. 找出还没有向量的数据（限制每次处理指定条数，防止 API 超时或内存溢出）
        // 检查 embedding_data 为 NULL 或空字符串的情况，且只处理未删除的记录
        String selectSql = "SELECT template_id, template_image_desc FROM biz_image_template " +
                "WHERE (embedding_data IS NULL OR embedding_data = '') AND template_image_desc IS NOT NULL AND deleted = 0 LIMIT ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(selectSql, limit);
        if (rows.isEmpty()) return 0;

        // 2. 提取文本列表
        List<String> texts = rows.stream()
                .map(row -> row.get("template_image_desc").toString())
                .collect(Collectors.toList());

        // 3. 调用 Qwen 批量生成向量
        TextEmbedding embedding = new TextEmbedding();
        TextEmbeddingParam param = TextEmbeddingParam.builder()
                .apiKey(getApiKey())
                .model("text-embedding-v3")
                .texts(texts)
                .build();

        TextEmbeddingResult result = embedding.call(param);
        List<List<Double>> vectors = result.getOutput().getEmbeddings().stream()
                .map(e -> e.getEmbedding())
                .collect(Collectors.toList());

        // 4. 批量写回数据库
        int count = 0;
        for (int i = 0; i < rows.size(); i++) {
            String templateId = (String) rows.get(i).get("template_id");
            String jsonVector = JSON.toJSONString(vectors.get(i));

            jdbcTemplate.update("UPDATE biz_image_template SET embedding_data = ? WHERE template_id = ?",
                    jsonVector, templateId);
            count++;
        }
        return count;
    }
}