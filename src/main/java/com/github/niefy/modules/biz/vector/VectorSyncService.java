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

import java.util.Collections;
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
     * 按优先级构建嵌入文本
     * 优先级：1、dish_category 2、price_display 3、product_type 4、template_image_desc 5、tags
     * @param row 数据库行数据
     * @return 组合后的文本
     */
    private String buildEmbeddingText(Map<String, Object> row) {
        StringBuilder text = new StringBuilder();
        
        // 1. dish_category
        Object dishCategory = row.get("dish_category");
        if (dishCategory != null && !dishCategory.toString().trim().isEmpty()) {
            text.append(dishCategory.toString().trim());
        }
        
        // 2. price_display
        Object priceDisplay = row.get("price_display");
        if (priceDisplay != null && !priceDisplay.toString().trim().isEmpty()) {
            if (text.length() > 0) text.append(" ");
            text.append(priceDisplay.toString().trim());
        }
        
        // 3. product_type
        Object productType = row.get("product_type");
        if (productType != null && !productType.toString().trim().isEmpty()) {
            if (text.length() > 0) text.append(" ");
            text.append(productType.toString().trim());
        }
        
        // 4. template_image_desc
        Object templateImageDesc = row.get("template_image_desc");
        if (templateImageDesc != null && !templateImageDesc.toString().trim().isEmpty()) {
            if (text.length() > 0) text.append(" ");
            text.append(templateImageDesc.toString().trim());
        }
        
        // 5. tags
        Object tags = row.get("tags");
        if (tags != null && !tags.toString().trim().isEmpty()) {
            if (text.length() > 0) text.append(" ");
            text.append(tags.toString().trim());
        }
        
        return text.toString();
    }

    /**
     * 初始化未向量化的数据
     * @param limit 每次处理的记录数限制
     * @return 成功处理的记录数
     */
    public int syncEmbeddings(int limit) throws Exception {
        // 1. 找出还没有向量的数据（限制每次处理指定条数，防止 API 超时或内存溢出）
        // 检查 embedding_data 为 NULL 或空字符串的情况，且只处理未删除的记录
        // 查询所有需要的业务字段
        String selectSql = "SELECT template_id, dish_category, price_display, product_type, template_image_desc, tags " +
                "FROM biz_image_template " +
                "WHERE (embedding_data IS NULL OR embedding_data = '') AND deleted = 0 " +
                "AND (dish_category IS NOT NULL OR price_display IS NOT NULL OR product_type IS NOT NULL OR template_image_desc IS NOT NULL OR tags IS NOT NULL) " +
                "LIMIT ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(selectSql, limit);
        if (rows.isEmpty()) return 0;

        // 2. 按优先级组合字段并提取文本列表
        List<String> texts = rows.stream()
                .map(this::buildEmbeddingText)
                .filter(text -> !text.trim().isEmpty()) // 过滤掉空文本
                .collect(Collectors.toList());
        
        // 如果所有文本都为空，则返回0
        if (texts.isEmpty()) return 0;

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
        // 注意：texts 和 rows 的数量可能不一致（因为过滤了空文本），需要重新匹配
        int count = 0;
        int vectorIndex = 0;
        for (Map<String, Object> row : rows) {
            String embeddingText = buildEmbeddingText(row);
            // 如果文本为空，跳过这条记录
            if (embeddingText.trim().isEmpty()) {
                continue;
            }
            
            String templateId = (String) row.get("template_id");
            String jsonVector = JSON.toJSONString(vectors.get(vectorIndex));

            jdbcTemplate.update("UPDATE biz_image_template SET embedding_data = ? WHERE template_id = ?",
                    jsonVector, templateId);
            count++;
            vectorIndex++;
        }
        return count;
    }

    /**
     * 刷新单条记录的向量数据
     * @param templateId 模板ID
     * @return 是否成功刷新
     * @throws Exception 异常
     */
    public boolean refreshEmbedding(String templateId) throws Exception {
        // 1. 查询该记录的所有业务字段
        String selectSql = "SELECT template_id, dish_category, price_display, product_type, template_image_desc, tags " +
                "FROM biz_image_template " +
                "WHERE template_id = ? AND deleted = 0";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(selectSql, templateId);
        if (rows.isEmpty()) {
            throw new RuntimeException("未找到模板ID为 " + templateId + " 的记录，或该记录已删除");
        }

        Map<String, Object> row = rows.get(0);
        String embeddingText = buildEmbeddingText(row);

        // 如果文本为空，清空向量数据
        if (embeddingText.trim().isEmpty()) {
            jdbcTemplate.update("UPDATE biz_image_template SET embedding_data = NULL WHERE template_id = ?", templateId);
            return true;
        }

        // 2. 调用 Qwen 生成向量
        TextEmbedding embedding = new TextEmbedding();
        TextEmbeddingParam param = TextEmbeddingParam.builder()
                .apiKey(getApiKey())
                .model("text-embedding-v3")
                .texts(Collections.singletonList(embeddingText))
                .build();

        TextEmbeddingResult result = embedding.call(param);
        List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();
        String jsonVector = JSON.toJSONString(vector);

        // 3. 更新数据库
        jdbcTemplate.update("UPDATE biz_image_template SET embedding_data = ? WHERE template_id = ?",
                jsonVector, templateId);

        return true;
    }

    /**
     * 清空单条记录的向量数据
     * @param templateId 模板ID
     * @return 是否成功清空
     */
    public boolean clearEmbedding(String templateId) {
        jdbcTemplate.update("UPDATE biz_image_template SET embedding_data = NULL WHERE template_id = ?", templateId);
        return true;
    }
}