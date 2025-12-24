package com.github.niefy.modules.wx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.niefy.modules.wx.entity.MsgSendProgress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息发送进度Mapper
 */
@Mapper
public interface MsgSendProgressMapper extends BaseMapper<MsgSendProgress> {
}

