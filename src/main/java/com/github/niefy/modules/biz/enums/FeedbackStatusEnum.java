package com.github.niefy.modules.biz.enums;

/**
 * 反馈处理状态枚举
 */
public enum FeedbackStatusEnum {
    /**
     * 待处理
     */
    PENDING(0),
    /**
     * 已处理
     */
    PROCESSED(1),
    /**
     * 已忽略
     */
    IGNORED(2);

    private int value;

    FeedbackStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static FeedbackStatusEnum of(int value) {
        for (FeedbackStatusEnum status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}

