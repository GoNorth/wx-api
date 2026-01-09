package com.github.niefy.modules.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.niefy.common.utils.Json;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 表单组件表
 *
 * @author niefy
 * @date 2026-01-09
 */
@Data
@TableName("biz_form_component")
public class BizFormComponent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String componentId;

    /**
     * 组件类型字典CODE：INPUT-单行输入框，TEXTAREA-多行输入框，SELECT-下拉框，DATE-日期选择器，DATETIME-日期时间选择器，NUMBER-数字输入框，SWITCH-开关，RADIO-单选框，CHECKBOX-多选框，UPLOAD-文件上传，RICH_TEXT-富文本编辑器
     */
    private String componentType;

    /**
     * 组件编码（唯一标识，用于前端识别）
     */
    private String componentCode;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 显示标签
     */
    private String componentLabel;

    /**
     * 组件描述
     */
    private String componentDesc;

    /**
     * 组件配置JSON，包含：placeholder占位符、required是否必填、defaultValue默认值、rules验证规则、props扩展属性等
     */
    private String componentConfig;

    /**
     * 选项配置JSON，格式：[{"label":"选项1","value":"value1"},{"label":"选项2","value":"value2"}]
     */
    private String optionsConfig;

    /**
     * 排序顺序，数字越小越靠前
     */
    private Integer sortOrder;

    /**
     * 状态字典CODE：ACTIVE-启用，INACTIVE-禁用
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

