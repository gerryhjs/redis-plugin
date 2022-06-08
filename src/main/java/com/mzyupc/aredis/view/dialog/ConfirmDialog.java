package com.mzyupc.aredis.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

/**
 * @author mzyupc@163.com
 * @date 2021/8/7 5:33 下午
 *
 * 确认提醒窗口
 */
public class ConfirmDialog extends DialogWrapper {
    private final String centerPanelText;
    private final Consumer<ActionEvent> customOkFunction;

    /**
     * @param project
     * @param title            对话框标题
     * @param centerPanelText  要提示的内容
     * @param customOkFunction 自定义的ok按钮功能
     */
    public ConfirmDialog(@NotNull Project project, String title, String centerPanelText, Consumer<ActionEvent> customOkFunction) {
        super(project);
        String text = "";
        switch (centerPanelText){
            case "Are you sure you want to delete these connections?":
                text = "您是否要删除此连接？";
                break;
            case "Are you sure you want to delete all the keys of the currently selected DB?":
                text = "您是否要清空当前数据库？";
                break;
            case "Are you sure you want to delete this key?":
                text = "您是否确认要删除选中的键？";
                break;
            case "Are you sure you want to remove this row?":
                text = "您是否确认要删除选中的行？";
                break;
            default:
                text = centerPanelText;
                break;
        }
        this.centerPanelText = text;
        this.customOkFunction = customOkFunction;
        this.setTitle("提示");
        this.setResizable(false);
        this.setAutoAdjustable(true);
        this.init();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        JLabel jLabel = new JLabel(centerPanelText);
        jLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        return jLabel;
    }

    /**
     * 覆盖默认的ok/cancel按钮
     *
     * @return
     */
    @NotNull
    @Override
    protected Action[] createActions() {
        DialogWrapperExitAction exitAction = new DialogWrapperExitAction("取消", CANCEL_EXIT_CODE);
        CustomOKAction okAction = new CustomOKAction();
        // 设置默认的焦点按钮
        okAction.putValue(DialogWrapper.DEFAULT_ACTION, true);
        return new Action[]{okAction, exitAction};
    }

    /**
     * 自定义 ok Action
     */
    protected class CustomOKAction extends DialogWrapperAction {
        protected CustomOKAction() {
            super("确认");
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (customOkFunction != null) {
                customOkFunction.accept(e);
            }
            close(OK_EXIT_CODE);
        }
    }
}

