package com.github.niefy.modules.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.biz.entity.BizTag;

import java.util.Map;

/**
 * 标签表
 *
 * @author niefy
 * @date 2026-01-07
 */
public interface BizTagService extends IService<BizTag> {
    /**
     * 分页查询标签数据
     * @param params 查询参数
     * @return IPage 分页结果
     */
    IPage<BizTag> queryPage(Map<String, Object> params);
}

