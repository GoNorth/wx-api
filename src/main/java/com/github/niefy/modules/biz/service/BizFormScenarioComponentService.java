package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizFormScenarioComponent;

import java.util.Map;

/**
 * 场景组件关联表
 *
 * @author niefy
 * @date 2026-01-09
 */
public interface BizFormScenarioComponentService extends IService<BizFormScenarioComponent> {
    /**
     * 分页查询关联数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizFormScenarioComponent> queryPage(Map<String, Object> params);
}

