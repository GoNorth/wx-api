package com.github.niefy.modules.biz.enums;

/**
 * 内容类型枚举
 */
public enum ContentTypeEnum {
    /**
     * 图片
     */
    IMAGE(1),
    /**
     * 视频
     */
    VIDEO(2);

    private int value;

    ContentTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static ContentTypeEnum of(int value) {
        for (ContentTypeEnum type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}

