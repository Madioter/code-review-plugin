package com.madiot.codereview.storage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.madiot.codereview.domain.CodeReviewPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用项目的xml配置文件存储记录
 * Created by Yi.Wang2 on 2016/11/24.
 */
@State(
        name = "XmlStorage",
        storages = {@Storage(
                file = "$PROJECT_CONFIG_DIR$/codePoints.xml"
        )}
)
public class XmlStorage implements PersistentStateComponent<XmlStorage> {

    public List<CodeReviewPoint> codeReviewPoints = new ArrayList<CodeReviewPoint>();

    private static final Logger LOG = Logger.getInstance(XmlStorage.class);

    public XmlStorage() {
    }

    @Override
    public XmlStorage getState() {
        return this;
    }

    public void loadState(XmlStorage state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static XmlStorage getInstance(Project project) {
        return ServiceManager.getService(project, XmlStorage.class);
    }

    public List<CodeReviewPoint> getData() {
        return codeReviewPoints;
    }
}
