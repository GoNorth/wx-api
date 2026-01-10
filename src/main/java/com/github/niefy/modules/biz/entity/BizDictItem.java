package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典项表
 *
 * @author niefy
 * @date 2025-01-08
 */
@Data
@TableName("biz_dict_item")
public class BizDictItem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String dictItemId;

    /**
     * 字典类型编码，关联biz_dict_type表
     */
    private String dictTypeCode;

    /**
     * 字典项编码（可选，用于扩展）
     */
    private String dictItemCode;

    /**
     * 字典项显示标签
     */
    private String dictItemLabel;

    /**
     * 字典项值（实际存储值）
     */
    private String dictItemValue;

    /**
     * 字典项描述
     */
    private String dictItemDesc;

    /**
     * 扩展数据（JSON格式，用于存储额外信息）
     */
    private String extData;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：ACTIVE-启用，INACTIVE-禁用
     */
    private String status;

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

