package com.github.niefy.common.handler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * JSON数组或字符串反序列化为JSON字符串
 * 用于处理前端可能发送数组[]或字符串""的情况
 *
 * @author niefy
 */
public class JsonArrayToStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.getCurrentToken();
        
        if (token == JsonToken.VALUE_STRING) {
            // 如果已经是字符串，直接返回
            return p.getText();
        } else if (token == JsonToken.START_ARRAY) {
            // 如果是数组，读取整个数组树结构并转换为JSON字符串
            com.fasterxml.jackson.databind.JsonNode node = p.getCodec().readTree(p);
            // 使用FastJSON转换为JSON字符串（与项目其他部分保持一致）
            // 先将Jackson的JsonNode转为字符串，再用FastJSON解析并重新序列化以确保格式一致
            String jsonStr = node.toString();
            Object parsed = com.alibaba.fastjson.JSON.parse(jsonStr);
            return com.alibaba.fastjson.JSON.toJSONString(parsed);
        } else if (token == JsonToken.VALUE_NULL) {
            // 如果是null，返回null
            return null;
        } else {
            // 其他类型，尝试读取为字符串
            return p.getValueAsString();
        }
    }
}

