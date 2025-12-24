package com.github.niefy.modules.wx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息发送进度记录
 * 用于记录混合消息的发送状态，支持断点续发
 * @author Nifury
 */
@Data
@TableName("wx_msg_send_progress")
public class MsgSendProgress implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 消息内容的hash code，用于标识同一条消息
     */
    private String contentHash;
    
    /**
     * 公众号appid
     */
    private String appid;
    
    /**
     * 用户openid
     */
    private String openid;
    
    /**
     * 原始消息内容（JSON格式，包含所有消息项）
     */
    private String originalContent;
    
    /**
     * 消息项列表（JSON格式）
     */
    private String messageItems;
    
    /**
     * 总消息数量
     */
    private Integer totalCount;
    
    /**
     * 已发送的消息数量（从0开始）
     */
    private Integer sentCount;
    
    /**
     * 是否已完成（0:未完成，1:已完成）
     */
    private Integer completed;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}

