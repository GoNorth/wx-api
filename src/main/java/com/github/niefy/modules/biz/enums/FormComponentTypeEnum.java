package com.github.niefy.modules.biz.enums;

/**
 * 表单组件类型枚举
 *
 * @author niefy
 * @date 2026-01-09
 */
public enum FormComponentTypeEnum {
    /**
     * 单行输入框
     */
    INPUT("INPUT", "单行输入框"),
    /**
     * 多行输入框
     */
    TEXTAREA("TEXTAREA", "多行输入框"),
    /**
     * 下拉框
     */
    SELECT("SELECT", "下拉框"),
    /**
     * 日期选择器
     */
    DATE("DATE", "日期选择器"),
    /**
     * 日期时间选择器
     */
    DATETIME("DATETIME", "日期时间选择器"),
    /**
     * 数字输入框
     */
    NUMBER("NUMBER", "数字输入框"),
    /**
     * 开关
     */
    SWITCH("SWITCH", "开关"),
    /**
     * 单选框
     */
    RADIO("RADIO", "单选框"),
    /**
     * 多选框
     */
    CHECKBOX("CHECKBOX", "多选框"),
    /**
     * 文件上传
     */
    UPLOAD("UPLOAD", "文件上传"),
    /**
     * 富文本编辑器
     */
    RICH_TEXT("RICH_TEXT", "富文本编辑器");

    private String value;
    private String desc;

    FormComponentTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    public static FormComponentTypeEnum of(String value) {
        if (value == null) {
            return null;
        }
        for (FormComponentTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}

