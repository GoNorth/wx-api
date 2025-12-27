package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizStoreVi;

import java.util.Map;

/**
 * 门店VI表
 *
 * @author niefy
 * @date 2024-12-27
 */
public interface BizStoreViService extends IService<BizStoreVi> {
    /**
     * 分页查询门店VI数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizStoreVi> queryPage(Map<String, Object> params);
}

