package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizDictItem;

import java.util.Map;

/**
 * 字典项表
 *
 * @author niefy
 * @date 2025-01-08
 */
public interface BizDictItemService extends IService<BizDictItem> {
    /**
     * 分页查询字典项数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizDictItem> queryPage(Map<String, Object> params);
}

