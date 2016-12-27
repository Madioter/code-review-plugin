package com.madiot.codereview.configuration;

import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.passwordSafe.PasswordSafeException;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;


/**
 * 全局配置文件XML操作类
 */
@State(
        name = "CodeReviewSettings",
        storages = {@Storage(
                file = "$APP_CONFIG$/codeReviewSetting.xml"
        )}
)
public class CodeReviewSettings implements PersistentStateComponent<CodeReviewSettings> {
    public String SERVER_URL = "";
    public String USERNAME = "";
    public static final String CRUCIBLE_SETTINGS_PASSWORD_KEY = "CRUCIBLE_SETTINGS_PASSWORD_KEY";
    private static final Logger LOG = Logger.getInstance(CodeReviewSettings.class);

    public CodeReviewSettings() {
    }

    public CodeReviewSettings getState() {
        return this;
    }

    public void loadState(CodeReviewSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static CodeReviewSettings getInstance() {
        return ServiceManager.getService(CodeReviewSettings.class);
    }

    public void savePassword(String pass) {
        try {
            PasswordSafe.getInstance().storePassword((Project) null, this.getClass(), "CRUCIBLE_SETTINGS_PASSWORD_KEY", pass);
        } catch (PasswordSafeException var3) {
            LOG.info("Couldn\'t get password for key [CRUCIBLE_SETTINGS_PASSWORD_KEY]", var3);
        }

    }

    @Nullable
    public String getPassword() {
        try {
            return PasswordSafe.getInstance().getPassword((Project) null, this.getClass(), "CRUCIBLE_SETTINGS_PASSWORD_KEY");
        } catch (PasswordSafeException var2) {
            LOG.info("Couldn\'t get the password for key [CRUCIBLE_SETTINGS_PASSWORD_KEY]", var2);
            return null;
        }
    }
}
