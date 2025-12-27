package com.github.niefy.modules.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.niefy.modules.biz.entity.BizResourcesContent;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源(图片视频)内容表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Mapper
// @CacheNamespace(flushInterval = 300000L)//缓存五分钟过期 - 临时禁用缓存用于调试
public interface BizResourcesContentMapper extends BaseMapper<BizResourcesContent> {

}

