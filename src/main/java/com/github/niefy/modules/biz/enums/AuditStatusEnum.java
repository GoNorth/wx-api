package com.github.niefy.modules.biz.enums;

/**
 * 审核状态枚举
 */
public enum AuditStatusEnum {
    /**
     * 待审核
     */
    PENDING(0),
    /**
     * 已通过
     */
    APPROVED(1),
    /**
     * 已拒绝
     */
    REJECTED(2);

    private int value;

    AuditStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static AuditStatusEnum of(int value) {
        for (AuditStatusEnum status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}

