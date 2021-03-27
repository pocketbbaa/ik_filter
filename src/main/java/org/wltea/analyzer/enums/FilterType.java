package org.wltea.analyzer.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * kg_sensitive_words 1
 * kg_key_words 2
 */
public enum FilterType {

    SENSITIVE_WORDS(1, "敏感词"),
    KEY_WORDS(2, "关键词");

    private int code;
    private String type;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    FilterType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public static FilterType getByCode(int code) {
        FilterType roles[] = FilterType.values();
        Optional<FilterType> optional = Arrays.stream(roles).filter(item -> item.code == code).findFirst();
        return optional.orElse(FilterType.SENSITIVE_WORDS);
    }

}
