package com.madiot.codereview.domain;

import com.madiot.codereview.common.ProblemType;
import com.madiot.codereview.common.SeverityLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yi.Wang2 on 2016/11/29.
 */
public class CodeReviewPointConstant {

    public static final String[] TABLE_COLUMN_NAMES = {"序号", "问题级别", "问题类型", "问题说明"};

    public static final int[] TABLE_COLUMN_WIDTH = {30, 70, 70, 500};

    public static final String[] EXPORT_COLUMN_NAMES = {"问题类型", "问题级别", "所在行", "选中代码", "问题描述", "问题所在位置", "文件相对位置"};

    public static final String SHEET_NAME = "代码评审记录";

    public static final String[] PROPERTY_NAMES = {"type", "level", "line", "context", "comment", "position", "path"};

    public static List<CodeReviewPoint> mapToCodeReviewPoint(List<Map<String, Object>> data) {
        List<CodeReviewPoint> result = new ArrayList<CodeReviewPoint>();
        for (Map<String, Object> point : data) {
            CodeReviewPoint codeReviewPoint = new CodeReviewPoint();
            for (String key : point.keySet()) {
                if (key.contains("B")) {
                    codeReviewPoint.setType(ProblemType.getByName((String) point.get(key)));
                } else if (key.contains("C")) {
                    codeReviewPoint.setLevel(SeverityLevel.getByName((String) point.get(key)));
                } else if (key.contains("E")) {
                    codeReviewPoint.setContext((String) point.get(key));
                } else if (key.contains("F")) {
                    codeReviewPoint.setComment((String) point.get(key));
                } else if (key.contains("G")) {
                    String position = (String) point.get(key);
                    String[] array = position.split(",");
                    for (String str : array) {
                        if (str.contains("select")) {
                            str = str.replace("select:", "");
                            String[] ps = str.split("-");
                            codeReviewPoint.setStart(Integer.parseInt(ps[0]));
                            codeReviewPoint.setEnd(Integer.parseInt(ps[1]));
                        } else if (str.contains("position")) {
                            str = str.replace("position:", "");
                            String[] ps = str.split(":");
                            codeReviewPoint.setLine(Integer.parseInt(ps[0]));
                            codeReviewPoint.setColumn(Integer.parseInt(ps[1]));
                        }
                    }
                } else if (key.contains("H")) {
                    codeReviewPoint.setPath((String) point.get(key));
                }
            }
            result.add(codeReviewPoint);
        }
        return result;
    }

    public static List<Map<String, Object>> toExportMap(List<CodeReviewPoint> cellData) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (cellData != null || !cellData.isEmpty()) {
            for (CodeReviewPoint point : cellData) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("type", point.getType().getName());
                item.put("level", point.getLevel().getName());
                item.put("line", "第" + point.getLine() + "行");
                item.put("context", point.getContext());
                item.put("comment", point.getComment());
                item.put("position", "select:" + point.getStart() + "-" + point.getEnd() + ",position:" + point.getLine() + ":" + point.getColumn());
                item.put("path", point.getPath());
                result.add(item);
            }
        }
        return result;
    }
}
