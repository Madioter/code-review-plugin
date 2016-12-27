package com.madiot.codereview.ui.dialog;

import com.intellij.ui.EnumComboBoxModel;
import com.madiot.codereview.common.ProblemType;
import com.madiot.codereview.common.SeverityLevel;
import com.madiot.codereview.domain.CodeReviewPoint;

import javax.swing.*;

/**
 * Created by Yi.Wang2 on 2016/11/25.
 */
public class CodeReviewDialog {
    private JPanel dialog;
    private JTextArea comment;
    private JComboBox severityLevels;
    private JComboBox problemTypes;

    private CodeReviewPoint codeReviewPoint;

    public CodeReviewDialog(CodeReviewPoint codeReviewPoint) {
        this.codeReviewPoint = codeReviewPoint;
        init();
        problemTypes.setSelectedItem(codeReviewPoint.getType());
        severityLevels.setSelectedItem(codeReviewPoint.getLevel());
        comment.setText(codeReviewPoint.getComment());
    }

    public void init() {
        ComboBoxModel problemTypeModel = new EnumComboBoxModel<ProblemType>(ProblemType.class);
        ComboBoxModel severityLevelModel = new EnumComboBoxModel<SeverityLevel>(SeverityLevel.class);
        problemTypes.setModel(problemTypeModel);
        severityLevels.setModel(severityLevelModel);
    }

    public JComponent getComponent() {
        return dialog;
    }

    public CodeReviewPoint getData() {
        codeReviewPoint.setLevel((SeverityLevel) severityLevels.getSelectedItem());
        codeReviewPoint.setType((ProblemType) problemTypes.getSelectedItem());
        codeReviewPoint.setComment(comment.getText());
        return codeReviewPoint;
    }
}
