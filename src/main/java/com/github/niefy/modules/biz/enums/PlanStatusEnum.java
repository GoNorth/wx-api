package com.github.niefy.modules.biz.enums;

/**
 * 计划状态枚举
 */
public enum PlanStatusEnum {
    /**
     * 初始待生成
     */
    INIT(0),
    /**
     * 修改确认中
     */
    CONFIRMING(1),
    /**
     * 已提交
     */
    SUBMITTED(2),
    /**
     * 已执行
     */
    EXECUTED(3),
    /**
     * 已下发
     */
    DELIVERED(4),
    /**
     * 已取消
     */
    CANCELLED(5);

    private int value;

    PlanStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PlanStatusEnum of(int value) {
        for (PlanStatusEnum status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}

