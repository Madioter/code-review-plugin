package com.madiot.codereview.util;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *   Title: ReadExcel.java
 *   Description:
 *   Copyright: Maple Copyright (c) 2013
 *   Company:
 * </pre>
 *
 * @author duanke
 * @version 1.0
 * @date 2013年12月31日
 */
public class ReadExcelUtil {
    /**
     * Excel 2003
     */
    public final static String XLS = "xls";

    /**
     * Excel 2007
     */
    public final static String XLSX = "xlsx";

    /**
     * 由Excel流的Sheet导出至List
     *
     * @param is
     * @param extensionName
     * @param sheetNum
     * @return
     * @throws IOException
     */
    public static List<Map<String, Object>> exportListFromExcel(InputStream is, String extensionName, int sheetNum) throws IOException {

        Workbook workbook = null;

        if (extensionName.toLowerCase().equals(XLS)) {
            workbook = new HSSFWorkbook(is);
        } else if (extensionName.toLowerCase().equals(XLSX)) {
            throw new IOException("不支持的excel文件类型：xls");
        }

        return readCell(workbook, sheetNum);
    }

    /**
     * 读取Cell的值
     *
     * @param workbook
     * @return
     */
    public static List<Map<String, Object>> readCell(Workbook workbook, int sheetNum) {
        Sheet sheet = workbook.getSheetAt(sheetNum);

        // 解析公式结果
        // FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        // 遍历所有行
        // for (Row row : sheet)
        // 除去表头即第一行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, Object> map = new HashMap<String, Object>();
            // 便利所有列
            for (Cell cell : row) {

                // 获取单元格的类型
                CellReference cellRef = new CellReference(row.getRowNum(), cell.getColumnIndex());
                String key = cellRef.formatAsString();

                switch (cell.getCellType()) {
                    // 字符串
                    case Cell.CELL_TYPE_STRING:
                        map.put(key, cell.getRichStringCellValue().getString());
                        break;
                    // 数字
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            map.put(key, cell.getDateCellValue());
                        } else {
                            map.put(key, cell.getNumericCellValue());
                        }
                        break;
                    // boolean
                    case Cell.CELL_TYPE_BOOLEAN:
                        map.put(key, cell.getBooleanCellValue());
                        break;
                    // 方程式
                    case Cell.CELL_TYPE_FORMULA:
                        map.put(key, cell.getCellFormula());
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        break;
                    case Cell.CELL_TYPE_ERROR:
                        break;
                    // 空值
                    default:
                        map.put(key, "");
                }
            }
            list.add(map);
        }
        return list;

    }
}
