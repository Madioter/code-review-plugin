package com.madiot.codereview.common;

/**
 * 严重级别
 * Created by Yi.Wang2 on 2016/11/25.
 */
public enum SeverityLevel {

    SLIGHT(0, "轻微"), WARNING(1, "警告"), ERROR(2, "错误");

    private int code;

    private String name;

    SeverityLevel(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SeverityLevel get(int code) {
        for (SeverityLevel level : SeverityLevel.values()) {
            if (level.code == code) {
                return level;
            }
        }
        return null;
    }

    public static SeverityLevel getByName(String name) {
        for (SeverityLevel level : SeverityLevel.values()) {
            if (level.name.equals(name)) {
                return level;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return getName();
    }
}
