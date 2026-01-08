package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片模板表
 *
 * @author niefy
 * @date 2025-01-06
 */
@Data
@TableName("biz_image_template")
public class BizImageTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String templateId;

    /**
     * 海报类型，如：爆款招牌
     */
    private String posterType;

    /**
     * 海报名称
     */
    private String posterName;

    /**
     * 业务状态，如：RECOG-识别中
     */
    private String status;

    /**
     * 菜品分类，如：炒菜
     */
    private String dishCategory;

    /**
     * 价格显示：有价格/无价格
     */
    private String priceDisplay;

    /**
     * 产品类型，如：单产品
     */
    private String productType;

    /**
     * 模板编号，根据poster_type、dish_category、price_display、product_type、create_time生成
     */
    private String templateNo;

    /**
     * 模板图片URL
     */
    private String templateImageUrl;

    /**
     * 模板图片文件名
     */
    private String templateImageName;

    /**
     * 模板图片描述
     */
    private String templateImageDesc;

    /**
     * 标签拼接字段，格式：蔬菜、叶装类、绿色、。。。
     */
    private String tags;

    /**
     * 识别任务ID
     */
    private String taskId;

    /**
     * 识别状态：pending-待处理，processing-处理中，completed-已完成，failed-失败
     */
    private String recognitionStatus;

    /**
     * 识别模型名称，如：Qwen-VL
     */
    private String recognitionModel;

    /**
     * 识别模型版本
     */
    private String recognitionModelVersion;

    /**
     * 多模态识别提示词/文本输入
     */
    private String recognitionPrompt;

    /**
     * 识别描述/识别结果文本（用于图生图的输入）
     */
    private String recognitionDesc;

    /**
     * 多模态提示词模板，支持变量替换（如：{dish_name}、{marketing_theme}）
     */
    private String multimodalPrompt;

    /**
     * 识别错误信息
     */
    private String recognitionErrorInfo;

    /**
     * 嵌入数据
     */
    private String embeddingData;

    /**
     * 识别完成时间
     */
    private Date recognitionCompleteTime;

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

