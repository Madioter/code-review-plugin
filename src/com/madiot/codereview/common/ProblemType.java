package com.madiot.codereview.common;

/**
 * Created by Yi.Wang2 on 2016/11/25.
 */
public enum ProblemType {

    NAME(0, "命名问题"), LOGIC(1, "逻辑错误"), OTHER(2, "其他问题");

    private int code;

    private String name;

    ProblemType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ProblemType get(int code) {
        for (ProblemType level : ProblemType.values()) {
            if (level.code == code) {
                return level;
            }
        }
        return null;
    }

    public static ProblemType getByName(String name) {
        for (ProblemType level : ProblemType.values()) {
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
