package com.lenovo.oj.enums;

import lombok.Getter;

@Getter
public enum JudgeLanguageEnum {
    JAVA("java"),
    CPP("cpp"),
    C("c");

    private final String value;

    JudgeLanguageEnum(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        for (JudgeLanguageEnum languageEnum : values()) {
            if (languageEnum.value.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
