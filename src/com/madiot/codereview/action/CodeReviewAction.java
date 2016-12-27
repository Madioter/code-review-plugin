package com.madiot.codereview.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.madiot.codereview.domain.CodeReviewPoint;
import com.madiot.codereview.storage.XmlStorage;
import com.madiot.codereview.ui.dialog.CodeReviewDialog;
import com.madiot.codereview.ui.panel.CodeReviewToolWindowPanel;
import com.madiot.codereview.ui.toolwindow.CodeReviewToolWindow;
import com.madiot.codereview.ui.toolwindow.CodeReviewToolWindowFactory;

/**
 * Created by Yi.Wang2 on 2016/11/10.
 */
public class CodeReviewAction extends AnAction {
    @Override
    public void actionPerformed(final AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();
        Document document = editor.getDocument();
        int start = selectionModel.getSelectionStart();
        int end = selectionModel.getSelectionEnd();
        document.createRangeMarker(start, end);

        document.getText().substring(start, end);

        VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        final Project project = event.getProject();

        VisualPosition visualPosition = editor.getCaretModel().getVisualPosition();
        CodeReviewPoint codeReviewPoint = new CodeReviewPoint();
        codeReviewPoint.setStart(start);
        codeReviewPoint.setEnd(end);
        TextRange range = new TextRange(document.getText().lastIndexOf("\n", start) + 1, document.getText().indexOf("\n", end));
        codeReviewPoint.setContext(document.getText(range).trim());
        codeReviewPoint.setLine(visualPosition.getLine());
        codeReviewPoint.setColumn(visualPosition.getColumn());
        codeReviewPoint.setPath(virtualFile.getPath().replace(project.getBaseDir().getPath(), ""));

        CodeReviewDialog dialog = new CodeReviewDialog(codeReviewPoint);
        DialogBuilder dialogBuilder = new DialogBuilder();
        dialogBuilder.setCenterPanel(dialog.getComponent());
        dialogBuilder.setTitle("code review edit");

        boolean isOk = dialogBuilder.show() == DialogWrapper.OK_EXIT_CODE;
        if (isOk) {
            codeReviewPoint = dialog.getData();
            XmlStorage xmlStorage = XmlStorage.getInstance(event.getProject());
            xmlStorage.codeReviewPoints.add(codeReviewPoint);
            CodeReviewToolWindowPanel panel = CodeReviewToolWindowFactory.get(project);
            if (panel != null) {
                panel.reloadList();
            }
        }
    }
}
