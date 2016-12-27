package com.madiot.codereview.ui.panel;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Yi.Wang2 on 2016/12/2.
 */
public class CodeReviewToolWindowPanel {
    private JButton clearButton;
    private JButton exportButton;
    private JButton importButton;
    private CodeTable codeList;
    private JPanel windowPanel;
    private JScrollPane tablePanel;

    private Project project;

    private final List<CodeReviewPoint> cellData;


    public CodeReviewToolWindowPanel(Project project) {
        this.project = project;
        this.cellData = XmlStorage.getInstance(project).codeReviewPoints;
        codeList.setData(cellData);
        this.initButton();
        this.initListener();
    }

    private void initButton() {
        clearButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int[] rows = codeList.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
                    cellData.remove(rows[i] - i);
                }
                codeList.reloadList(cellData);
            }
        });

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
                fc.showOpenDialog(fc);
                File file = fc.getSelectedFile();
                try {
                    InputStream fileInputStream = new FileInputStream(file);
                    List<Map<String, Object>> readList = ReadExcelUtil.exportListFromExcel(fileInputStream, ReadExcelUtil.XLS, 0);
                    List<CodeReviewPoint> points = CodeReviewPointConstant.mapToCodeReviewPoint(readList);
                    addAll(points);
                    codeList.reloadList(cellData);
                } catch (Exception e1) {
                    Messages.showErrorDialog(project, "文件解析异常，文件路径：" + file.getAbsolutePath(), "error");
                    return;
                }
            }
        });
    }

    private void addAll(List<CodeReviewPoint> points) {
        for (CodeReviewPoint item : points) {
            if (!cellData.contains(item)) {
                cellData.add(item);
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
                        codeList.reloadList(cellData);
                    }
                } else if (e.getClickCount() == 1) {
                    int row = ((JTable) e.getSource()).rowAtPoint(e.getPoint()); //获得行位置
                    List<CodeReviewPoint> cellData = XmlStorage.getInstance(project).codeReviewPoints;
                    CodeReviewPoint codeReviewPoint = cellData.get(row);
                    VisualPosition visualPosition = new VisualPosition(codeReviewPoint.getLine(), codeReviewPoint.getColumn());
                    VirtualFile virtualFile = project.getBaseDir().findFileByRelativePath(codeReviewPoint.getPath());
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

    public JComponent getComponent() {
        return windowPanel;
    }

    public void reloadList() {
        codeList.reloadList(cellData);
    }
}
