package com.github.niefy.modules.biz.enums;

/**
 * 计划类型枚举
 */
public enum PlanTypeEnum {
    /**
     * 图片计划
     */
    IMAGE(1),
    /**
     * 视频计划
     */
    VIDEO(2);

    private int value;

    PlanTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PlanTypeEnum of(int value) {
        for (PlanTypeEnum type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}

