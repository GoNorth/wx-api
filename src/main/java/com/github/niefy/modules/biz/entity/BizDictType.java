package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典类型表
 *
 * @author niefy
 * @date 2025-01-08
 */
@Data
@TableName("biz_dict_type")
public class BizDictType implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String dictTypeId;

    /**
     * 字典类型编码（唯一，如：DISH_CATEGORY）
     */
    private String dictTypeCode;

    /**
     * 字典类型名称（如：菜品分类）
     */
    private String dictTypeName;

    /**
     * 字典类型描述
     */
    private String dictTypeDesc;

    /**
     * 分类（如：FORM-表单控件，PROMPT-提示词，BIZ-业务）
     */
    private String category;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：ACTIVE-启用，INACTIVE-禁用
     */
    private String status;

    /**
     * 是否允许即时创建：1-允许，0-不允许
     */
    private Integer allowCreate;

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

