package com.github.niefy.modules.biz.vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vector")
public class VectorController {

    @Autowired
    private VectorSyncService vectorSyncService;

    @GetMapping("/init")
    public String init(@RequestParam(defaultValue = "1") int limit,
                      @RequestParam(defaultValue = "false") boolean processAll) {
        try {
            int totalProcessed = 0;
            int batchSize;
            
            if (processAll) {
                // 循环处理直到没有空数据（生产环境建议开启异步线程执行）
                do {
                    batchSize = vectorSyncService.syncEmbeddings(limit);
                    totalProcessed += batchSize;
                } while (batchSize > 0);
            } else {
                // 只处理一次，处理指定数量的记录
                batchSize = vectorSyncService.syncEmbeddings(limit);
                totalProcessed = batchSize;
            }

            return "初始化完成，共处理记录数: " + totalProcessed;
        } catch (Exception e) {
            e.printStackTrace();
            return "初始化失败: " + e.getMessage();
        }
    }
}