package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.biz.dao.BizImageProductMapper;
import com.github.niefy.modules.biz.entity.BizImageProduct;
import com.github.niefy.modules.biz.service.BizImageProductService;
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
 * 产品图片表
 *
 * @author niefy
 * @date 2025-01-06
 */
@Service
public class BizImageProductServiceImpl extends ServiceImpl<BizImageProductMapper, BizImageProduct> implements BizImageProductService {
    private static final Logger logger = LoggerFactory.getLogger(BizImageProductServiceImpl.class);

    @Autowired
    private TosStorageService storageService;

    @Override
    public IPage<BizImageProduct> queryPage(Map<String, Object> params) {
        String productId = (String) params.get("productId");
        String templateId = (String) params.get("templateId");
        String dishName = (String) params.get("dishName");
        String dishCategory = (String) params.get("dishCategory");
        String productType = (String) params.get("productType");
        String generateStatus = (String) params.get("generateStatus");
        String generateTaskId = (String) params.get("generateTaskId");

        return this.page(
            new Query<BizImageProduct>().getPage(params),
            new QueryWrapper<BizImageProduct>()
                .eq(StringUtils.hasText(productId), "product_id", productId)
                .eq(StringUtils.hasText(templateId), "template_id", templateId)
                .like(StringUtils.hasText(dishName), "dish_name", dishName)
                .eq(StringUtils.hasText(dishCategory), "dish_category", dishCategory)
                .eq(StringUtils.hasText(productType), "product_type", productType)
                .eq(StringUtils.hasText(generateStatus), "generate_status", generateStatus)
                .eq(StringUtils.hasText(generateTaskId), "generate_task_id", generateTaskId)
                .eq("deleted", 0)
                .orderByDesc("create_time")
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithFile(BizImageProduct bizImageProduct, MultipartFile productImageFile) throws Exception {
        // 设置默认值
        if (bizImageProduct.getProductId() == null || bizImageProduct.getProductId().isEmpty()) {
            bizImageProduct.setProductId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (bizImageProduct.getDeleted() == null) {
            bizImageProduct.setDeleted(0);
        }
        if (bizImageProduct.getCreateTime() == null) {
            bizImageProduct.setCreateTime(new Date());
        }
        bizImageProduct.setUpdateTime(new Date());

        // 上传产品图片文件
        if (productImageFile != null && !productImageFile.isEmpty()) {
            String originalFilename = Objects.requireNonNull(productImageFile.getOriginalFilename());
            String fileNameWithoutExt;
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0) {
                fileNameWithoutExt = originalFilename.substring(0, lastDotIndex);
            } else {
                fileNameWithoutExt = originalFilename;
            }

            String cosFileName = fileNameWithoutExt + "_" + bizImageProduct.getProductId();
            String fileUrl = storageService.storeWithoutLogo(productImageFile, cosFileName);
            bizImageProduct.setProductImageUrl(fileUrl);
            bizImageProduct.setProductImageName(originalFilename);
            logger.info("产品图片上传COS成功: {}", fileUrl);
        }

        // 保存或更新
        this.saveOrUpdate(bizImageProduct);
    }
}

