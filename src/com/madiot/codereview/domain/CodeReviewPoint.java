package com.madiot.codereview.domain;

import com.intellij.openapi.editor.VisualPosition;
import com.madiot.codereview.common.ProblemType;
import com.madiot.codereview.common.SeverityLevel;

/**
 * Created by Yi.Wang2 on 2016/11/25.
 */
public class CodeReviewPoint {

    /**
     * 文件地址
     */
    private String path;

    /**
     * 标记起始位置
     */
    private Integer start;

    /**
     * 标记结束位置
     */
    private Integer end;

    /**
     * 级别
     */
    private SeverityLevel level;

    /**
     * 类型
     */
    private ProblemType type;

    /**
     * 上下文
     */
    private String context;

    /**
     * 备注
     */
    private String comment;

    /**
     * 当前文档位置行
     */
    private int line;

    /**
     * 文档位置：列
     */
    private int column;

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path.
     *
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets start.
     *
     * @return the start
     */
    public Integer getStart() {
        return start;
    }

    /**
     * Sets start.
     *
     * @param start the start
     */
    public void setStart(Integer start) {
        this.start = start;
    }

    /**
     * Gets end.
     *
     * @return the end
     */
    public Integer getEnd() {
        return end;
    }

    /**
     * Sets end.
     *
     * @param end the end
     */
    public void setEnd(Integer end) {
        this.end = end;
    }

    /**
     * Gets level.
     *
     * @return the level
     */
    public SeverityLevel getLevel() {
        return level;
    }

    /**
     * Sets level.
     *
     * @param level the level
     */
    public void setLevel(SeverityLevel level) {
        this.level = level;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public ProblemType getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(ProblemType type) {
        this.type = type;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public String getContext() {
        return context;
    }

    /**
     * Sets context.
     *
     * @param context the context
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Gets comment.
     *
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets comment.
     *
     * @param comment the comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets line.
     *
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * Sets line.
     *
     * @param line the line
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Gets column.
     *
     * @return the column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Sets column.
     *
     * @param column the column
     */
    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        CodeReviewPoint point = (CodeReviewPoint) obj;
        if (point.getColumn() == this.getColumn() && point.getLine() == this.getLine() && point.getComment() == point.getComment()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.getColumn() + this.getLine() + this.getComment().hashCode();
    }
}
