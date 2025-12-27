package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizStore;

import java.util.Map;

/**
 * 门店表
 *
 * @author niefy
 * @date 2024-12-27
 */
public interface BizStoreService extends IService<BizStore> {
    /**
     * 分页查询门店数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizStore> queryPage(Map<String, Object> params);
}

