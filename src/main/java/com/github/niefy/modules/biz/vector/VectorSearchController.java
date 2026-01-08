package com.github.niefy.modules.biz.vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/vector")
public class VectorSearchController {

    @Autowired
    private VectorSearchService vectorSearchService;

    /**
     * 搜索接口
     * 例：GET /api/vector/search?keyword=苹果&posterType=爆款招牌
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String keyword,
                                      @RequestParam(defaultValue = "10") int limit,
                                      @RequestParam(required = false) String posterType) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> searchResults = vectorSearchService.search(keyword, limit, posterType);
            result.put("success", true);
            result.put("data", searchResults);
            result.put("count", searchResults != null ? searchResults.size() : 0);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("data", Collections.emptyList());
        }
        return result;
    }

    /**
     * 手动刷新缓存接口（当数据库新增了向量数据后调用）
     */
    @GetMapping("/refresh")
    public Map<String, Object> refresh() {
        Map<String, Object> result = new HashMap<>();
        try {
            vectorSearchService.reloadCache();
            result.put("success", true);
            result.put("message", "缓存已刷新");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
}