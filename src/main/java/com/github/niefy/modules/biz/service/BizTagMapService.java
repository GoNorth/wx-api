package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizTagMap;

import java.util.Map;

/**
 * 标签映射表（标签与模板的关联表）
 *
 * @author niefy
 * @date 2026-01-07
 */
public interface BizTagMapService extends IService<BizTagMap> {
    /**
     * 分页查询标签映射数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizTagMap> queryPage(Map<String, Object> params);
}

