package com.github.niefy.modules.biz.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.niefy.modules.biz.entity.BizTagMap;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签映射表（标签与模板的关联表）
 *
 * @author niefy
 * @date 2026-01-07
 */
@Mapper
public interface BizTagMapMapper extends BaseMapper<BizTagMap> {

}

