package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizImageProduct;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 产品图片表
 *
 * @author niefy
 * @date 2025-01-06
 */
public interface BizImageProductService extends IService<BizImageProduct> {
    /**
     * 分页查询产品图片数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizImageProduct> queryPage(Map<String, Object> params);

    /**
     * 保存产品图片（包括文件上传）
     * @param bizImageProduct 产品图片信息
     * @param productImageFile 产品图片文件
     * @throws Exception 异常
     */
    void saveWithFile(BizImageProduct bizImageProduct, MultipartFile productImageFile) throws Exception;
}

