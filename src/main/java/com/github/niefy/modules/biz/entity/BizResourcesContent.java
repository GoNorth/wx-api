package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 资源(图片视频)内容表
 *
 * @author niefy
 * @date 2024-12-27
 */
@Data
@TableName("biz_resources_content")
public class BizResourcesContent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String contentId;

    /**
     * 门店ID，冗余字段用于查询便利，逻辑上通过PLAN_ITEM_ID间接关联
     */
    private String storeId;

    /**
     * 内容类型字典CODE：1-IMAGE图片，2-VIDEO视频
     */
    private Integer contentType;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 文件URL，图片或视频
     */
    private String fileUrl;

    /**
     * 视频时长，单位秒，仅视频类型使用
     */
    private Integer duration;

    /**
     * 适用场景或用途说明，如：适用于会员充值推广、适用于会员日活动推广等
     */
    private String applicableScenario;

    /**
     * 发布日期
     */
    private Date publishDate;

    /**
     * 关联的计划项目ID，主要关联，通过此字段间接关联到门店，每个计划项目可以生成多次内容
     */
    private String planItemId;

    /**
     * 资源来源：UPLOAD-用户上传，GENERATE-程序生成
     */
    private String fromType;

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

