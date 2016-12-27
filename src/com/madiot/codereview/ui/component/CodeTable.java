package com.madiot.codereview.ui.component;

import com.madiot.codereview.domain.CodeReviewPoint;
import com.madiot.codereview.domain.CodeReviewPointConstant;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Yi.Wang2 on 2016/11/28.
 */
public class CodeTable extends JTable {

    protected CodeDataModel dataModel;

    protected CodeColumnModel columnModel;

    public CodeTable() {
        this(null);
    }

    public CodeTable(List<CodeReviewPoint> cellData) {
        super();
        this.columnModel = new CodeColumnModel();
        getTableHeader().setColumnModel(columnModel);
        setColumnModel(columnModel);
        if (cellData != null) {
            this.dataModel = new CodeDataModel(cellData);
        } else {
            this.dataModel = new CodeDataModel();
        }
        setModel(dataModel);
        setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
        this.columnModel.setColumnWidth();
        this.resizeAndRepaint();
    }

    public void setData(List<CodeReviewPoint> cellData) {
        int index = 0;
        if (cellData != null) {
            for (CodeReviewPoint point : cellData) {
                index++;
                dataModel.addRow(new Object[]{index, point.getType().getName(), point.getLevel().getName(), point.getComment()});
            }
        }
    }

    public void clearRows() {
        getModel().clearAll();
    }

    public void reloadList(List<CodeReviewPoint> cellData) {
        getSelectionModel().clearSelection();
        clearRows();
        if (cellData != null && !cellData.isEmpty()) {
            setData(cellData);
        }
    }

    public void clearAll() {
        getModel().clearAll();
    }

    public void removeSelected() {
        int[] rows = getSelectedRows();
        getModel().removeRows(rows);
    }

    @Override
    public CodeDataModel getModel() {
        return dataModel;
    }

    public class CodeDataModel extends DefaultTableModel {

        public CodeDataModel() {
            super(CodeReviewPointConstant.TABLE_COLUMN_NAMES, 0);
        }

        public CodeDataModel(List<CodeReviewPoint> cellData) {
            super(CodeReviewPointConstant.TABLE_COLUMN_NAMES, 0);
            setData(cellData);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public void addRow(Object[] rowData) {
            super.addRow(rowData);
        }

        public void clearAll() {
            dataVector.clear();
            if (getRowCount() > 0) {
                fireTableRowsDeleted(0, getRowCount() - 1);
            }
        }

        @Override
        public void removeRow(int row) {
            super.removeRow(row);
        }

        public void removeRows(int[] rows) {
            Arrays.sort(rows);
            for (int i = 0; i < rows.length; i++) {
                removeRow(rows[i] - i);
            }
        }
    }

    public class CodeColumnModel extends DefaultTableColumnModel {

        public CodeColumnModel() {
            super();
            String[] columnNames = CodeReviewPointConstant.TABLE_COLUMN_NAMES;
            for (int i = 0; i < columnNames.length; i++) {
                TableColumn tableColumn = new TableColumn();
                tableColumn.setHeaderValue(columnNames[i]);
                addColumn(tableColumn);
            }
        }

        public void setColumnWidth() {
            int[] columnWidths = CodeReviewPointConstant.TABLE_COLUMN_WIDTH;
            for (int i = 0; i < columnWidths.length; i++) {
                getColumn(i).setMinWidth(columnWidths[i] - 30);
                getColumn(i).setPreferredWidth(columnWidths[i]);
            }
        }
    }
}
