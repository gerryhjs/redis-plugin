package com.mzyupc.aredis.view.dialog;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LoadingDecorator;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.NumberDocument;
import com.intellij.ui.treeStructure.Tree;
import com.mzyupc.aredis.utils.PropertyUtil;
import com.mzyupc.aredis.utils.RedisPoolManager;
import com.mzyupc.aredis.view.ConnectionManager;
import com.mzyupc.aredis.vo.ConnectionInfo;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

/**
 * @author mzyupc@163.com
 */
public class ConnectionSettingsDialog extends DialogWrapper implements Disposable {

    JTextField nameTextField;
    JTextField hostField;
    JTextField portField;
    JTextField passwordField;
    private PropertyUtil propertyUtil;
    private String connectionId;
    private CustomOKAction okAction;
    private Tree connectionTree;
    private ConnectionManager connectionManager;

    /**
     * if connectionId is blank ? New Connection : Edit Connection
     * @param project
     * @param connectionId
     * @param connectionTree
     */
    public ConnectionSettingsDialog(Project project, String connectionId, Tree connectionTree, ConnectionManager connectionManager) {
        super(project);
        this.propertyUtil = PropertyUtil.getInstance(project);
        this.connectionId = connectionId;
        this.connectionTree = connectionTree;
        this.connectionManager = connectionManager;
        this.setTitle("连接设置");
        this.setSize(600, 300);
        this.init();
    }

    @Override
    protected void init() {
        super.init();
    }

    /**
     * 新建连接的对话框
     *
     * @return
     */
    @Override
    protected @Nullable
    JComponent createCenterPanel() {
        ConnectionInfo connection = propertyUtil.getConnection(connectionId);
        boolean newConnection = connection == null;

        // TODO 参数校验, 输入框下面展示提示
        nameTextField = new JTextField(newConnection ? null : connection.getName());
        nameTextField.setToolTipText("Name");

        // url port 输入框
        hostField = new JTextField(newConnection ? null : connection.getUrl());
        hostField.setToolTipText("Host");
        portField = new JTextField();
        portField.setToolTipText("Port");
        portField.setDocument(new NumberDocument());
        portField.setText(newConnection ? null : connection.getPort());

        // password输入框
        passwordField = new JTextField(newConnection ? null : connection.getPassword());

        // 显示密码
        JCheckBox checkBox = new JCheckBox("Show Password");
        checkBox.addItemListener(e -> {
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                passwordField.setEchoChar((char) 0);
//            } else {
//                passwordField.setEchoChar('*');
//            }
        });
        checkBox.setBounds(300, 81, 135, 27);

        // 测试连接按钮
        JButton testButton = new JButton("测试");

        JTextPane testResult = new JTextPane();
        testResult.setMargin(new Insets(0, 10, 0, 0));
        testResult.setOpaque(false);
        testResult.setEditable(false);
        testResult.setFocusable(false);
        testResult.setAlignmentX(SwingConstants.LEFT);


        LoadingDecorator loadingDecorator = new LoadingDecorator(testResult, this, 0);

        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ValidationInfo validationInfo = doValidate(true);
                if (validationInfo != null) {
                    ErrorDialog.show(validationInfo.message);
                } else {
                    String password;
                    if (StringUtils.isNotBlank(new String(passwordField.getText()))) {
                        password = new String(passwordField.getText());
                    } else {
                        password = null;
                    }

                    loadingDecorator.startLoading(false);
                    ApplicationManager.getApplication().invokeLater(()->{
                        RedisPoolManager.TestConnectionResult testConnectionResult = RedisPoolManager.getTestConnectionResult(hostField.getText(), Integer.parseInt(portField.getText()), password);
                        testResult.setText(testConnectionResult.getMsg());
                        if (testConnectionResult.isSuccess()) {
                            testResult.setText("连接成功");
                            testResult.setForeground(JBColor.GREEN);
                        } else {
                            testResult.setForeground(JBColor.RED);
                        }
                    });
                    loadingDecorator.stopLoading();
                }
            }
        });

        // 使用 GridBagLayout 布局
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        JPanel connectionSettingsPanel = new JPanel();
        connectionSettingsPanel.setLayout(gridBagLayout);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.15;
        constraints.weighty = 0.33;
        JLabel connectionNameLabel = new JLabel("名称:");
        gridBagLayout.setConstraints(connectionNameLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        constraints.weightx = 0.85;
        constraints.weighty = 0.33;
        gridBagLayout.setConstraints(nameTextField, constraints);

        connectionSettingsPanel.add(connectionNameLabel);
        connectionSettingsPanel.add(nameTextField);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.15;
        constraints.weighty = 0.33;
        JLabel hostLabel = new JLabel("主机:");
        gridBagLayout.setConstraints(hostLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.85;
        constraints.weighty = 0.2;
        gridBagLayout.setConstraints(hostField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.15;
        constraints.weighty = 0.33;
        JLabel portLabel = new JLabel("端口:");
        gridBagLayout.setConstraints(portLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.85;
        constraints.weighty = 0.2;
        gridBagLayout.setConstraints(portField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.1;
        constraints.weighty = 0.33;
        JLabel splitLabel = new JLabel(" ");
        gridBagLayout.setConstraints(splitLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.weightx = 0.15;
        constraints.weighty = 0.33;
        JLabel passwordLabel = new JLabel("密码:");
        gridBagLayout.setConstraints(passwordLabel, constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.gridheight = 2;
        constraints.weightx = 0.85;
        constraints.weighty = 0.33;
        gridBagLayout.setConstraints(passwordField, constraints);

//        constraints.gridx = 3;
//        constraints.gridy = 2;
//        constraints.gridwidth = 1;
//        constraints.gridheight = 1;
//        constraints.weightx = 0.15;
//        constraints.weighty = 0.33;
//        gridBagLayout.setConstraints(checkBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.weightx = 0.1;
        constraints.weighty = 0.33;
        JLabel splitLabel2 = new JLabel("\r\n");
        gridBagLayout.setConstraints(splitLabel, constraints);

        connectionSettingsPanel.add(hostLabel);
        connectionSettingsPanel.add(hostField);
        connectionSettingsPanel.add(portLabel);
        connectionSettingsPanel.add(portField);
//        connectionSettingsPanel.add(splitLabel);
        connectionSettingsPanel.add(passwordLabel);
        connectionSettingsPanel.add(passwordField);
//        connectionSettingsPanel.add(testButton);
//        connectionSettingsPanel.add(splitLabel);
//        connectionSettingsPanel.add(checkBox);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(testButton);
        JPanel testConnectionSettingsPanel = new JPanel(new GridLayout(3, 1));
        testConnectionSettingsPanel.add(splitLabel);
        testConnectionSettingsPanel.add(row);
        testConnectionSettingsPanel.add(loadingDecorator.getComponent());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(connectionSettingsPanel, BorderLayout.NORTH);
        centerPanel.add(testConnectionSettingsPanel, BorderLayout.SOUTH);
        return centerPanel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return nameTextField;
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
        okAction = new CustomOKAction();

        // 设置默认的焦点按钮
        okAction.putValue(DialogWrapper.DEFAULT_ACTION, true);
        return new Action[]{okAction, exitAction};
    }

    /**
     * 校验数据
     *
     * @return 通过必须返回null，不通过返回一个 ValidationInfo 信息
     */
    @Nullable
    protected ValidationInfo doValidate(boolean isTest) {
        if (!isTest) {
            if (StringUtils.isBlank(nameTextField.getText())) {
                return new ValidationInfo("连接名称不能为空");
            }
        }
        if (StringUtils.isBlank(hostField.getText())) {
            return new ValidationInfo("主机不能为空");
        }
        String port = portField.getText();
        if (StringUtils.isBlank(port)) {
            return new ValidationInfo("端口不能为空");
        }
        if (!StringUtils.isNumeric(port)) {
            return new ValidationInfo("端口必须是数字");
        }
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
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
            // 点击ok的时候进行数据校验
            ValidationInfo validationInfo = doValidate(false);
            if (validationInfo != null) {
                ErrorDialog.show(validationInfo.message);
            } else {
                DefaultTreeModel connectionTreeModel = (DefaultTreeModel) connectionTree.getModel();
                if (StringUtils.isEmpty(connectionId)) {
                    // 保存connection
                    String password = null;
                    if (StringUtils.isNotBlank(new String(passwordField.getText()))) {
                        password = new String(passwordField.getText());
                    }
                    // 持久化连接信息
                    ConnectionInfo connectionInfo = ConnectionInfo.builder()
                            .name(nameTextField.getText())
                            .url(hostField.getText())
                            .port(portField.getText())
                            .password(password)
                            .build();
                    propertyUtil.saveConnection(connectionInfo);
                    // connectionTree 中添加节点
                    connectionManager.addConnectionToList(connectionTreeModel, connectionInfo);
                    close(CANCEL_EXIT_CODE);

                } else {
                    // 更新connection
                    String password = null;
                    if (StringUtils.isNotBlank(new String(passwordField.getText()))) {
                        password = new String(passwordField.getText());
                    }
                    ConnectionInfo connectionInfo = ConnectionInfo.builder()
                            .id(connectionId)
                            .name(nameTextField.getText())
                            .url(hostField.getText())
                            .port(portField.getText())
                            .password(password)
                            .build();
                    // 更新redisPoolMgr
                    RedisPoolManager redisPoolManager = new RedisPoolManager(connectionInfo);
                    connectionManager.getConnectionRedisMap().put(connectionId, redisPoolManager);
                    // 设置connectionNode的connectionInfo
                    TreePath selectionPath = connectionTree.getSelectionPath();
                    DefaultMutableTreeNode connectionNode = (DefaultMutableTreeNode) selectionPath.getPath()[1];
                    connectionNode.setUserObject(connectionInfo);
                    // 重新载入connectionNode
                    connectionTreeModel.reload(connectionNode);
                    // 更新持久化信息
                    propertyUtil.removeConnection(connectionId, redisPoolManager);
                    propertyUtil.saveConnection(connectionInfo);

                    close(OK_EXIT_CODE);
                }
            }
        }
    }

}
