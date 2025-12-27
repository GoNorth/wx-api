package com.github.niefy.modules.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.niefy.modules.biz.entity.BizPlanItem;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 计划项目表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Mapper
@CacheNamespace(flushInterval = 300000L)//缓存五分钟过期
public interface BizPlanItemMapper extends BaseMapper<BizPlanItem> {

}

