package com.madiot.codereview.ui.toolwindow;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.madiot.codereview.domain.CodeReviewPoint;
import com.madiot.codereview.domain.CodeReviewPointConstant;
import com.madiot.codereview.domain.ExcelEntity;
import com.madiot.codereview.storage.XmlStorage;
import com.madiot.codereview.ui.component.CodeTable;
import com.madiot.codereview.ui.dialog.CodeReviewDialog;
import com.madiot.codereview.util.ExportExcelUtil;
import com.madiot.codereview.util.ReadExcelUtil;
import org.apache.commons.httpclient.util.DateUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Yi.Wang2 on 2016/11/24.
 */
public class CodeReviewToolWindow extends SimpleToolWindowPanel {
    private JButton exportButton;
    private JTable codeList;
    private JPanel myToolWindowContent;
    private JButton clearButton;
    private JButton clearAllButton;
    private JButton importButton;
    private JScrollPane header;

    private Project project;

    public CodeReviewToolWindow(Project project) {
        super(false);
        this.project = project;
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        codeList.setModel(model);
        this.setList(project);
        this.initButton();
    }

    public void reloadList(Project project) {
        List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
        DefaultTableModel model = (DefaultTableModel) codeList.getModel();
        clearRows(model);
        if (cellData != null && !cellData.isEmpty()) {
            setData(model, cellData);
        }
    }

    private void setList(Project project) {
        List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
        DefaultTableModel model = (DefaultTableModel) codeList.getModel();
        initList(model);
        setData(model, cellData);
    }

    private void initButton() {
        clearButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int[] rows = codeList.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
                    cellData.remove(rows[i] - i);
                }
                reloadList(project);
            }
        });
        /*clearAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
                cellData.clear();
                reloadList(project);
            }
        });*/
        clearAllButton.setEnabled(false);

        exportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Excel导出");
                fc.setFileFilter(new FileNameExtensionFilter("xls", "xls"));
                fc.setAcceptAllFileFilterUsed(false);
                fc.setSelectedFile(new File(project.getName() + "_" + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss") + ".xls"));
                fc.showSaveDialog(fc);
                File file = fc.getSelectedFile();

                String fname = fc.getName(file);
                if (fname != null && fname.trim().length() > 0) {
                    if (!fname.endsWith(".xml"))
                        file = new File(file.getParentFile(), fname);
                }

                OutputStream fileOutputStream = null;
                List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
                ExcelEntity excelEntity = new ExcelEntity();
                excelEntity.setColumnNames(CodeReviewPointConstant.EXPORT_COLUMN_NAMES);
                excelEntity.setSheetName(CodeReviewPointConstant.SHEET_NAME);
                excelEntity.setPropertyNames(CodeReviewPointConstant.PROPERTY_NAMES);
                excelEntity.setResultList(CodeReviewPointConstant.toExportMap(cellData));
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (Exception e1) {
                        Messages.showErrorDialog(project, "文件创建异常，文件路径：" + file.getAbsolutePath(), "error");
                        return;
                    }
                } else {
                    int value = Messages.showOkCancelDialog(project, "该文件已经存在，确定要覆盖吗？", "提示", null);
                    if (value != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                try {
                    fileOutputStream = new FileOutputStream(file);
                    ExportExcelUtil.exportExcel(excelEntity, fileOutputStream);
                } catch (Exception e1) {
                    Messages.showErrorDialog(project, "文件导出异常：" + e1.getMessage(), "error");
                }
            }
        });

        importButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Excel导出");
                fc.setFileFilter(new FileNameExtensionFilter("xls", "xls"));
                fc.setAcceptAllFileFilterUsed(false);
                int returnVal = fc.showOpenDialog(fc);
                File file = fc.getSelectedFile();
                try {
                    InputStream fileInputStream = new FileInputStream(file);
                    List<Map<String, Object>> readList = ReadExcelUtil.exportListFromExcel(fileInputStream, ReadExcelUtil.XLS, 0);
                    List<CodeReviewPoint> points = CodeReviewPointConstant.mapToCodeReviewPoint(readList);
                    XmlStorage xmlStorage = XmlStorage.getInstance(project);
                    addAll(points);
                    reloadList(project);
                } catch (Exception e1) {
                    Messages.showErrorDialog(project, "文件解析异常，文件路径：" + file.getAbsolutePath(), "error");
                    return;
                }
            }
        });
    }

    private void addAll(List<CodeReviewPoint> points) {
        XmlStorage xmlStorage = XmlStorage.getInstance(project);
        for (CodeReviewPoint item : points) {
            if (!xmlStorage.codeReviewPoints.contains(item)) {
                xmlStorage.codeReviewPoints.add(item);
            }
        }
    }

    private void initList(DefaultTableModel model) {
        String[] columnNames = CodeReviewPointConstant.TABLE_COLUMN_NAMES;

        JTableHeader header = codeList.getTableHeader();
        TableColumnModel tableColumnModel = new DefaultTableColumnModel();
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn tableColumn = new TableColumn();
            tableColumn.setHeaderValue(columnNames[i]);
            tableColumnModel.addColumn(tableColumn);
        }
        header.setColumnModel(tableColumnModel);
        model.setColumnCount(columnNames.length);
        fitTableColumns(codeList);
        initListener();
    }

    private void setData(DefaultTableModel model, List<CodeReviewPoint> cellData) {
        int index = 0;
        for (CodeReviewPoint point : cellData) {
            index++;
            model.addRow(new Object[]{index, point.getType(), point.getLevel(), point.getComment()});
        }
    }

    private void clearRows(DefaultTableModel model) {
        int rowCount = model.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            model.removeRow(0);
        }
    }

    private void fitTableColumns(JTable myTable) {
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < myTable.getColumnCount(); i++) {
            TableColumn tc = myTable.getColumn(myTable.
                    getColumnName(i));
            int c = tc.getModelIndex();
            int width = 0;
            int maxw = 0;
            for (int r = 0; r < myTable.getRowCount(); ++r) {
                TableCellRenderer renderer = myTable.
                        getCellRenderer(r, c); //得到每个单元格的渲染器
                Component comp = renderer.getTableCellRendererComponent(
                        myTable, "", false, false,
                        r, c);
                width = comp.getPreferredSize().width;
                maxw = width > maxw ? width : maxw;
            }
            TableCellRenderer headRenderer = tc.getHeaderRenderer(); //得到每列中的表头渲染器
            if (headRenderer == null) {
                headRenderer = myTable.getTableHeader().         //得到表头中的表头渲染器
                        getDefaultRenderer();
            }
            Component comp = headRenderer.getTableCellRendererComponent(
                    myTable, tc.getHeaderValue(), false, false, 0,
                    i);
            int w = comp.getPreferredSize().width;
            maxw = w > maxw ? w : maxw;
            if (myTable.getColumnCount() == 2) {
                tc.setMinWidth(maxw + 20);
                tc.setPreferredWidth(maxw + 160);
            }
            if (myTable.getColumnCount() == 3) {
                tc.setMinWidth(maxw + 20);                //列设置宽度
                tc.setPreferredWidth(maxw + 70);
            }
        }
    }

    public void initListener() {
        codeList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    //实现双击 {
                    int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); //获得行位置
                    List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
                    CodeReviewPoint codeReviewPoint = cellData.get(row);
                    final CodeReviewDialog dialog = new CodeReviewDialog(codeReviewPoint);
                    DialogBuilder dialogBuilder = new DialogBuilder();
                    dialogBuilder.setCenterPanel(dialog.getComponent());
                    dialogBuilder.setTitle("code review edit");
                    boolean isOk = dialogBuilder.show() == DialogWrapper.OK_EXIT_CODE;
                    if (isOk) {
                        dialog.getData();
                        reloadList(project);
                    }
                } else if (e.getClickCount() == 1) {
                    int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); //获得行位置
                    List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
                    CodeReviewPoint codeReviewPoint = cellData.get(row);
                    VisualPosition visualPosition = new VisualPosition(codeReviewPoint.getLine(), codeReviewPoint.getColumn());
                    VirtualFile virtualFile = project.getBaseDir().findFileByRelativePath("/src/CheckStyleFilePanel.java");
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    if (fileEditorManager.isFileOpen(virtualFile)) {
                        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile);
                        Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
                        editor.getCaretModel().moveToVisualPosition(visualPosition);
                        editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
                        editor.getSelectionModel().setSelection(codeReviewPoint.getStart(), codeReviewPoint.getEnd());
                    }
                }
            }
        });
    }
}
