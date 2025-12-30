package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizSales;

import java.util.Map;

/**
 * 销售员表
 *
 * @author niefy
 * @date 2025-01-01
 */
public interface BizSalesService extends IService<BizSales> {
    /**
     * 分页查询销售员数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizSales> queryPage(Map<String, Object> params);
}

