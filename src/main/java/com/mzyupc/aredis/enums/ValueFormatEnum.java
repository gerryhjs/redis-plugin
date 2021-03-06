package com.mzyupc.aredis.enums;

/**
 * @author mzyupc@163.com
 * @date 2021/8/19 9:46 下午
 */
public enum ValueFormatEnum {
    /**
     *
     */
    PLAIN("Plain text"),
    JSON("JSON"),
    XML("XML"),
    HTML("HTML");

    private String description;

    public String getDescription() {
        return description;
    }

    private ValueFormatEnum (String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }

}
