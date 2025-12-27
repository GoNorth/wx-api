package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 内容反馈表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Data
@TableName("biz_content_feedback")
public class BizContentFeedback implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String feedbackId;

    /**
     * 内容ID
     */
    private String contentId;

    /**
     * 反馈类型字典CODE：CHARACTER-人物错误，PRODUCT-产品错误，TEXT-文字错误
     */
    private String feedbackType;

    /**
     * 反馈描述，可选
     */
    private String feedbackDesc;

    /**
     * 用户微信OPENID
     */
    private String userOpenid;

    /**
     * 处理状态字典CODE：0-PENDING待处理，1-PROCESSED已处理，2-IGNORED已忽略
     */
    private Integer status;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 逻辑删除标记字典CODE：0-NOT_DELETED未删除，1-DELETED已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }
}

