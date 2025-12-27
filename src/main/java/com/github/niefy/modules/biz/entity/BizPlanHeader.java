package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 计划头部表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Data
@TableName("biz_plan_header")
public class BizPlanHeader implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String planId;

    /**
     * 门店ID，所属门店，建立所属关系
     */
    private String storeId;

    /**
     * 计划类型字典CODE：1-IMAGE图片计划，2-VIDEO视频计划
     */
    private Integer planType;

    /**
     * 策略类型字典CODE：1-PRIVATE_DOMAIN私域复购，2-PUBLIC_DOMAIN公域获客
     */
    private Integer strategyType;

    /**
     * 计划名称，可选
     */
    private String planName;

    /**
     * 状态字典CODE：0-INIT初始待生成，1-CONFIRMING修改确认中，2-SUBMITTED已提交，3-EXECUTED已执行，4-DELIVERED已下发，5-CANCELLED已取消
     */
    private Integer status;

    /**
     * 提交时间
     */
    private Date submittedAt;

    /**
     * 执行时间
     */
    private Date executedAt;

    /**
     * 下发用户时间
     */
    private Date deliveredAt;

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

