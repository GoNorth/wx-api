package com.github.niefy.modules.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.niefy.modules.biz.entity.BizStoreCharacter;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店人物形象表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Mapper
// @CacheNamespace(flushInterval = 300000L)//缓存五分钟过期 - 临时禁用缓存用于调试
public interface BizStoreCharacterMapper extends BaseMapper<BizStoreCharacter> {

}

