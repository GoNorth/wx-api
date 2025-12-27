package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizPlanHeader;

import java.util.Map;

/**
 * 计划头部表
 *
 * @author niefy
 * @date 2024-12-27
 */
public interface BizPlanHeaderService extends IService<BizPlanHeader> {
    /**
     * 分页查询计划头部数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizPlanHeader> queryPage(Map<String, Object> params);
}

