package com.github.niefy.modules.wx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.niefy.modules.wx.entity.MsgSendProgress;

/**
 * 消息发送进度服务
 */
public interface MsgSendProgressService extends IService<MsgSendProgress> {
    /**
     * 保存或更新发送进度
     * @param contentHash 消息内容hash
     * @param appid 公众号appid
     * @param openid 用户openid
     * @param originalContent 原始内容
     * @param messageItems 消息项列表（JSON格式）
     * @param totalCount 总消息数量
     * @param sentCount 已发送数量
     * @param completed 是否已完成
     */
    void saveOrUpdateProgress(String contentHash, String appid, String openid, 
                              String originalContent, String messageItems, 
                              Integer totalCount, Integer sentCount, Integer completed);

    /**
     * 根据hash和openid查询发送进度
     * @param contentHash 消息内容hash
     * @param openid 用户openid
     * @return 发送进度记录
     */
    MsgSendProgress getProgress(String contentHash, String openid);

    /**
     * 根据openid查询未完成的发送进度（最近一条）
     * @param openid 用户openid
     * @return 发送进度记录
     */
    MsgSendProgress getLatestUncompletedProgress(String openid);
}

