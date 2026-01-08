package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.niefy.common.handler.JsonArrayToStringDeserializer;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 产品图片表
 *
 * @author niefy
 * @date 2025-01-06
 */
@Data
@TableName("biz_image_product")
public class BizImageProduct implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String productId;

    /**
     * 模板ID，关联biz_image_template表
     */
    private String templateId;

    /**
     * 海报类型（来自biz_image_template表），如：爆款招牌
     */
    @TableField(exist = false) // 映射模板表字段
    private String posterType;

    /**
     * 菜品分类（来自biz_image_template表），如：炒菜
     */
    @TableField(exist = false) // 映射模板表字段
    private String templateDishCategory;

    /**
     * 菜品名称
     */
    private String dishName;

    /**
     * 图片文件名
     */
    private String imageFileName;

    /**
     * 价格（元）
     */
    private BigDecimal price;

    /**
     * 营销主题，如：新春特惠
     */
    private String marketingTheme;

    /**
     * 产品图片URL
     */
    private String productImageUrl;

    /**
     * 产品图片文件名
     */
    private String productImageName;

    /**
     * 图生图任务ID
     */
    private String generateTaskId;

    /**
     * 图生图状态：pending-待处理，processing-处理中，completed-已完成，failed-失败
     */
    private String generateStatus;

    /**
     * 图生图模型名称
     */
    private String generateModel;

    /**
     * 图生图模型版本
     */
    private String generateModelVersion;

    /**
     * 图生图提示词（基于模板识别结果文本生成）
     */
    private String generatePrompt;

    /**
     * 图生图参数配置，JSON格式存储
     */
    private String generateParams;

    /**
     * 生成的图片URL列表，JSON格式存储
     */
    @JsonDeserialize(using = JsonArrayToStringDeserializer.class)
    private String generatedImages;

    /**
     * 生成的图片数量
     */
    private Integer generatedImageCount;

    /**
     * 图生图错误信息
     */
    private String generateErrorInfo;

    /**
     * 图生图完成时间
     */
    private Date generateCompleteTime;

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

