package com.madiot.codereview.configuration;

//import com.crucible.codereview.connection.CrucibleTestConnectionTask;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 全局配置，Setting中的编辑页面
 */
public class CodeReviewConfigurable implements SearchableConfigurable {
    private JPanel myMainPanel;
    private JTextField myServerField;
    private JTextField myUsernameField;
    private JPasswordField myPasswordField;
    private JButton myTestButton;
    private final CodeReviewSettings myCrucibleSettings;
    private static final String DEFAULT_PASSWORD_TEXT = "************";
    private boolean myPasswordModified;

    public CodeReviewConfigurable() {
        this.myCrucibleSettings = CodeReviewSettings.getInstance();
        this.myTestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CodeReviewConfigurable.this.saveSettings();
                /*CrucibleTestConnectionTask testConnectionTask = new CrucibleTestConnectionTask(ProjectManager.getInstance().getDefaultProject());
                ProgressManager.getInstance().run(testConnectionTask);*/
            }
        });
        this.myPasswordField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                CodeReviewConfigurable.this.myPasswordModified = true;
            }

            public void removeUpdate(DocumentEvent e) {
                CodeReviewConfigurable.this.myPasswordModified = true;
            }

            public void changedUpdate(DocumentEvent e) {
                CodeReviewConfigurable.this.myPasswordModified = true;
            }
        });
    }

    private void saveSettings() {
        this.myCrucibleSettings.USERNAME = this.myUsernameField.getText();
        this.myCrucibleSettings.SERVER_URL = this.myServerField.getText();
        if (this.isPasswordModified()) {
            this.myCrucibleSettings.savePassword(String.valueOf(this.myPasswordField.getPassword()));
        }

    }

    @NotNull
    public String getId() {
        return "CrucibleConfigurable";
    }

    @Nullable
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    public String getDisplayName() {
        return "Code Review";
    }

    @Nullable
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    public JComponent createComponent() {
        return this.myMainPanel;
    }

    public boolean isModified() {
        if (this.isPasswordModified()) {
            String password = this.myCrucibleSettings.getPassword();
            if (!StringUtil.equals(password, new String(this.myPasswordField.getPassword()))) {
                return true;
            }
        }

        return !StringUtil.equals(this.myCrucibleSettings.SERVER_URL, this.myServerField.getText()) || !StringUtil.equals(this.myCrucibleSettings.USERNAME, this.myUsernameField.getText());
    }

    public void apply() throws ConfigurationException {
        this.saveSettings();
    }

    public void reset() {
        this.myUsernameField.setText(this.myCrucibleSettings.USERNAME);
        this.myPasswordField.setText(StringUtil.isEmptyOrSpaces(this.myUsernameField.getText()) ? "" : "************");
        this.resetPasswordModification();
        this.myServerField.setText(this.myCrucibleSettings.SERVER_URL);
    }

    public boolean isPasswordModified() {
        return this.myPasswordModified;
    }

    public void resetPasswordModification() {
        this.myPasswordModified = false;
    }

    public void disposeUIResources() {
    }
}

