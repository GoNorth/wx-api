package com.github.niefy.modules.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.niefy.modules.biz.entity.BizImageProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 产品图片表
 *
 * @author niefy
 * @date 2025-01-06
 */
@Mapper
public interface BizImageProductMapper extends BaseMapper<BizImageProduct> {

    /**
     * 分页查询产品图片（关联模板表）
     * @param page 分页对象
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<BizImageProduct> queryPageWithTemplate(Page<BizImageProduct> page, @Param("productId") String productId,
                                                  @Param("templateId") String templateId,
                                                  @Param("dishName") String dishName,
                                                  @Param("generateStatus") String generateStatus,
                                                  @Param("generateTaskId") String generateTaskId);
}

