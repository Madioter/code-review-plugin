package com.madiot.codereview.ui.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.madiot.codereview.ui.panel.CodeReviewToolWindowPanel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yi.Wang2 on 2016/11/30.
 */
public class CodeReviewToolWindowFactory implements ToolWindowFactory {

    private static Map<Project, CodeReviewToolWindowPanel> mapper = new HashMap<Project, CodeReviewToolWindowPanel>();

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        CodeReviewToolWindowPanel codeReviewToolWindow = new CodeReviewToolWindowPanel(project);
        mapper.put(project, codeReviewToolWindow);
        Content content = contentFactory.createContent(codeReviewToolWindow.getComponent(), "", false);
        content.setCloseable(false);
        toolWindow.getContentManager().addContent(content);
    }

    public static CodeReviewToolWindowPanel get(Project project) {
        return mapper.get(project);
    }
}
