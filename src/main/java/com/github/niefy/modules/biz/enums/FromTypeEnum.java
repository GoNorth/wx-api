package com.github.niefy.modules.biz.enums;

/**
 * 资源来源类型枚举
 */
public enum FromTypeEnum {
    /**
     * 用户上传
     */
    UPLOAD("UPLOAD"),
    /**
     * 程序生成
     */
    GENERATE("GENERATE");

    private String code;

    FromTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static FromTypeEnum of(String code) {
        for (FromTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}

