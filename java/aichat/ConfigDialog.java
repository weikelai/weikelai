package aichat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 配置对话框类
 * 用于配置API密钥、URL、模型等参数
 */
public class ConfigDialog extends JDialog {
    private ApiConfig config;
    private boolean saved = false;
    
    private JTextField apiKeyField;
    private JTextField apiUrlField;
    private JTextField modelField;
    private JSpinner maxTokensSpinner;
    private JSpinner fontSizeSpinner;
    
    /**
     * 构造方法
     * @param parent 父窗口
     * @param config API配置
     */
    public ConfigDialog(JFrame parent, ApiConfig config) {
        super(parent, "API配置", true);
        this.config = config;
        
        initComponents();
        loadConfig();
        
        setSize(500, 280);
        setLocationRelativeTo(parent);
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // API密钥
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("API密钥:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        apiKeyField = new JTextField(30);
        mainPanel.add(apiKeyField, gbc);
        
        // API URL
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("API地址:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        apiUrlField = new JTextField(30);
        mainPanel.add(apiUrlField, gbc);
        
        // 模型名称
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("模型名称:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        modelField = new JTextField(30);
        mainPanel.add(modelField, gbc);
        
        // 最大Token数
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("最大Token:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        maxTokensSpinner = new JSpinner(new SpinnerNumberModel(1024, 1, 4096, 128));
        mainPanel.add(maxTokensSpinner, gbc);
        
        // 字体大小
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("字体大小:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(14, 10, 24, 1));
        mainPanel.add(fontSizeSpinner, gbc);
        gbc.gridx = 2;
        mainPanel.add(new JLabel("px"), gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");
        
        saveButton.addActionListener(e -> saveConfig());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 加载配置到界面
     */
    private void loadConfig() {
        apiKeyField.setText(config.getApiKey());
        apiUrlField.setText(config.getApiUrl());
        modelField.setText(config.getModel());
        maxTokensSpinner.setValue(config.getMaxTokens());
        fontSizeSpinner.setValue(config.getFontSize());
    }
    
    /**
     * 保存配置
     */
    private void saveConfig() {
        config.setApiKey(apiKeyField.getText().trim());
        config.setApiUrl(apiUrlField.getText().trim());
        config.setModel(modelField.getText().trim());
        config.setMaxTokens((Integer) maxTokensSpinner.getValue());
        config.setFontSize((Integer) fontSizeSpinner.getValue());
        
        try {
            config.saveToFile();
            saved = true;
            JOptionPane.showMessageDialog(this, "配置保存成功！", "提示", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "保存配置失败: " + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 是否已保存
     * @return 是否已保存
     */
    public boolean isSaved() {
        return saved;
    }
    
    /**
     * 获取配置
     * @return API配置
     */
    public ApiConfig getConfig() {
        return config;
    }
}


