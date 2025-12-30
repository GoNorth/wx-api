package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 计划项目表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Data
@TableName("biz_plan_item")
public class BizPlanItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String itemId;

    /**
     * 计划头部ID
     */
    private String planId;

    /**
     * 策略类型字典CODE：1-PRIVATE_DOMAIN私域复购，2-PUBLIC_DOMAIN公域获客
     */
    private Integer strategyType;

    /**
     * 日期键：图片计划为YYYY-MM-DD，视频计划为WEEK1-WEEK4
     */
    private String dateKey;

    /**
     * 时段字典CODE：ALL-全天，BREAKFAST-早餐，LUNCH-午餐，AFTERNOON-下午，DINNER-晚餐
     */
    private String timeSlot;

    /**
     * 营销主题字典CODE：MEMBER-会员，DISCOUNT-折扣，NEWPRODUCT-新品，GROUPBUY-团购，HOLIDAY-节日，REFERRAL-推荐等
     */
    private String marketingTheme;

    /**
     * 发布平台字典CODE：DOUYIN-抖音，MEITUAN-美团，XIAOHONGSHU-小红书，WECHAT-微信等，公域获客时使用
     */
    private String platform;

    /**
     * 发布平台描述（非数据库字段，基于 platform CODE 转换）
     */
    @TableField(exist = false)
    private String platformDesc;

    /**
     * 内容类型标签字典CODE：SHORTVIDEO-短视频，GROUPBUY-团购，STOREVISIT-到店，NEWPRODUCT-新品等，公域获客时使用
     */
    private String contentTag;

    /**
     * 内容类型标签描述（非数据库字段，基于 contentTag CODE 转换）
     */
    @TableField(exist = false)
    private String contentTagDesc;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 优惠价
     */
    private BigDecimal discountPrice;

    /**
     * 活动详情与规则
     */
    private String activityDetails;

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

