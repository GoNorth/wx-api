package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizFormScenario;

import java.util.Map;

/**
 * 表单场景表
 *
 * @author niefy
 * @date 2026-01-09
 */
public interface BizFormScenarioService extends IService<BizFormScenario> {
    /**
     * 分页查询场景数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizFormScenario> queryPage(Map<String, Object> params);
}

