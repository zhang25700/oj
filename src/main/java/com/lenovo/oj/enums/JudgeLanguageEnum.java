package com.lenovo.oj.enums;

import lombok.Getter;

@Getter
/**
 * 平台支持的判题语言枚举。
 *
 * 控制层和服务层会通过这个枚举统一校验 language 参数，
 * 避免把未知语言直接传给沙箱执行。
 */
public enum JudgeLanguageEnum {
    JAVA("java"),
    CPP("cpp"),
    C("c");

    private final String value;

    JudgeLanguageEnum(String value) {
        this.value = value;
    }

    /**
     * 判断前端传入的语言标识是否在平台支持范围内。
     */
    public static boolean isValid(String value) {
        for (JudgeLanguageEnum languageEnum : values()) {
            if (languageEnum.value.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
