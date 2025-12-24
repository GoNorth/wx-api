package com.github.niefy.modules.wx.dao;

import com.github.niefy.modules.wx.entity.WxMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信素材
 * 
 * @author niefy
 * @date 2024-12-24
 */
@Mapper
@CacheNamespace(flushInterval = 300000L)//缓存五分钟过期
public interface WxMaterialMapper extends BaseMapper<WxMaterial> {
	
}

