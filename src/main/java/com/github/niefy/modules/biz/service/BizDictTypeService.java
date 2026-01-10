package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizDictType;

import java.util.Map;

/**
 * 字典类型表
 *
 * @author niefy
 * @date 2025-01-08
 */
public interface BizDictTypeService extends IService<BizDictType> {
    /**
     * 分页查询字典类型数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizDictType> queryPage(Map<String, Object> params);
}

