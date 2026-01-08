package com.github.niefy.modules.biz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Autowired
    private BizImageProductMapper bizImageProductMapper;

    @Override
    public IPage<BizImageProduct> queryPage(Map<String, Object> params) {
        String productId = (String) params.get("productId");
        String templateId = (String) params.get("templateId");
        String dishName = (String) params.get("dishName");
        String generateStatus = (String) params.get("generateStatus");
        String generateTaskId = (String) params.get("generateTaskId");

        // 使用自定义查询方法，关联查询模板表的poster_type和dish_category字段
        Page<BizImageProduct> page = (Page<BizImageProduct>) new Query<BizImageProduct>().getPage(params);
        return bizImageProductMapper.queryPageWithTemplate(
            page,
            StringUtils.hasText(productId) ? productId : null,
            StringUtils.hasText(templateId) ? templateId : null,
            StringUtils.hasText(dishName) ? dishName : null,
            StringUtils.hasText(generateStatus) ? generateStatus : null,
            StringUtils.hasText(generateTaskId) ? generateTaskId : null
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

