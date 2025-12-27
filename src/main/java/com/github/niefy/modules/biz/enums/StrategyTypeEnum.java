package com.github.niefy.modules.biz.enums;

/**
 * 策略类型枚举
 */
public enum StrategyTypeEnum {
    /**
     * 私域复购
     */
    PRIVATE_DOMAIN(1),
    /**
     * 公域获客
     */
    PUBLIC_DOMAIN(2);

    private int value;

    StrategyTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static StrategyTypeEnum of(int value) {
        for (StrategyTypeEnum type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}

