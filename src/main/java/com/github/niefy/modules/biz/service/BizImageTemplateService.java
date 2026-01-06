package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizImageTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 图片模板表
 *
 * @author niefy
 * @date 2025-01-06
 */
public interface BizImageTemplateService extends IService<BizImageTemplate> {
    /**
     * 分页查询图片模板数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizImageTemplate> queryPage(Map<String, Object> params);

    /**
     * 保存图片模板（包括文件上传）
     * @param bizImageTemplate 图片模板信息
     * @param templateImageFile 模板图片文件
     * @throws Exception 异常
     */
    void saveWithFile(BizImageTemplate bizImageTemplate, MultipartFile templateImageFile) throws Exception;
}

