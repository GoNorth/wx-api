package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizFormComponent;

import java.util.Map;

/**
 * 表单组件表
 *
 * @author niefy
 * @date 2026-01-09
 */
public interface BizFormComponentService extends IService<BizFormComponent> {
    /**
     * 分页查询组件数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizFormComponent> queryPage(Map<String, Object> params);
}

