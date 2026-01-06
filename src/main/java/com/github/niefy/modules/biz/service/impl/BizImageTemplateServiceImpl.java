package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizImageTemplateMapper;
import com.github.niefy.modules.biz.entity.BizImageTemplate;
import com.github.niefy.modules.biz.enums.ImageTemplateStatusEnum;
import com.github.niefy.modules.biz.service.BizImageTemplateService;
import com.github.niefy.modules.oss.service.TosStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 图片模板表
 *
 * @author niefy
 * @date 2025-01-06
 */
@Service
public class BizImageTemplateServiceImpl extends ServiceImpl<BizImageTemplateMapper, BizImageTemplate> implements BizImageTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(BizImageTemplateServiceImpl.class);

    @Autowired
    private TosStorageService storageService;

    @Override
    public IPage<BizImageTemplate> queryPage(Map<String, Object> params) {
        String templateId = (String) params.get("templateId");
        String posterType = (String) params.get("posterType");
        String status = (String) params.get("status");
        String recognitionStatus = (String) params.get("recognitionStatus");
        String recognitionModel = (String) params.get("recognitionModel");
        String taskId = (String) params.get("taskId");

        return this.page(
            new Query<BizImageTemplate>().getPage(params),
            new QueryWrapper<BizImageTemplate>()
                .eq(StringUtils.hasText(templateId), "template_id", templateId)
                .eq(StringUtils.hasText(posterType), "poster_type", posterType)
                .eq(StringUtils.hasText(status), "status", status)
                .eq(StringUtils.hasText(recognitionStatus), "recognition_status", recognitionStatus)
                .eq(StringUtils.hasText(recognitionModel), "recognition_model", recognitionModel)
                .eq(StringUtils.hasText(taskId), "task_id", taskId)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFile(BizImageTemplate bizImageTemplate, MultipartFile templateImageFile) throws Exception {
        // 设置默认值
        if (bizImageTemplate.getTemplateId() == null || bizImageTemplate.getTemplateId().isEmpty()) {
            bizImageTemplate.setTemplateId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizImageTemplate.getStatus() == null || bizImageTemplate.getStatus().isEmpty()) {
            bizImageTemplate.setStatus(ImageTemplateStatusEnum.INIT.getValue());
        }
        if (bizImageTemplate.getDeleted() == null) {
            bizImageTemplate.setDeleted(0);
        }
        if (bizImageTemplate.getCreateTime() == null) {
            bizImageTemplate.setCreateTime(new Date());
        }
        bizImageTemplate.setUpdateTime(new Date());

        // 上传模板图片文件
        if (templateImageFile != null && !templateImageFile.isEmpty()) {
            String originalFilename = Objects.requireNonNull(templateImageFile.getOriginalFilename());
            String fileNameWithoutExt;
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                fileNameWithoutExt = originalFilename.substring(0, lastDotIndex);
            } else {
                fileNameWithoutExt = originalFilename;
            }

            String cosFileName = fileNameWithoutExt + "_" + bizImageTemplate.getTemplateId();
            String fileUrl = storageService.storeWithoutLogo(templateImageFile, cosFileName);
            bizImageTemplate.setTemplateImageUrl(fileUrl);
            bizImageTemplate.setTemplateImageName(originalFilename);
            logger.info("模板图片上传COS成功: {}", fileUrl);
        }

        // 保存或更新
        this.saveOrUpdate(bizImageTemplate);
    }
}

