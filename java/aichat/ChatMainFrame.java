package aichat;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * 主窗口类
 * 提供AI对话的图形界面
 */
public class ChatMainFrame extends JFrame {
    private ConversationHistory history;
    private ApiConfig config;
    private ApiClient apiClient;
    
    // GUI组件
    private JEditorPane chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton loadButton;
    private JButton configButton;
    private JButton exportTextButton;
    private JButton copyButton;
    private JButton clearInputButton;
    private JLabel fontSizeLabel;
    private JSpinner fontSizeSpinner;
    private JLabel statusLabel;
    
    /**
     * 构造方法
     */
    public ChatMainFrame() {
        // 初始化配置和历史
        config = ApiConfig.loadFromFile();
        history = new ConversationHistory();
        apiClient = new ApiClient(config);
        
        initComponents();
        setupLayout();
        setupEvents();
        
        setTitle("AI智能对话助手");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // 检查API配置
        if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
            appendSystemMessage("提示: 请先点击\"设置\"按钮配置API密钥");
        }
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        // 聊天显示区域 - 使用JEditorPane以支持HTML/Markdown渲染
        chatArea = new JEditorPane();
        chatArea.setEditable(false);
        chatArea.setContentType("text/html");
        chatArea.setEditorKit(new HTMLEditorKit());
        
        // 设置样式（字体大小将从配置中读取）
        StyleSheet styleSheet = ((HTMLDocument) chatArea.getDocument()).getStyleSheet();
        int fontSize = config.getFontSize();
        styleSheet.addRule("body { font-family: 'Microsoft YaHei', sans-serif; font-size: " + fontSize + "px; margin: 5px; }");
        styleSheet.addRule("code { background-color: #f4f4f4; padding: 2px 4px; border-radius: 3px; font-family: 'Consolas', 'Monaco', monospace; }");
        styleSheet.addRule("pre { background-color: #f4f4f4; padding: 10px; border-radius: 5px; overflow-x: auto; }");
        styleSheet.addRule("pre code { background-color: transparent; padding: 0; }");
        styleSheet.addRule("h1, h2, h3, h4, h5, h6 { margin-top: 10px; margin-bottom: 5px; }");
        styleSheet.addRule("ul, ol { margin-left: 20px; }");
        styleSheet.addRule("li { margin: 3px 0; }");
        styleSheet.addRule("a { color: #0066cc; text-decoration: none; }");
        styleSheet.addRule("a:hover { text-decoration: underline; }");
        styleSheet.addRule("blockquote { border-left: 4px solid #ccc; margin-left: 0; padding-left: 15px; color: #666; }");
        styleSheet.addRule("table { border-collapse: collapse; width: 100%; margin: 10px 0; }");
        styleSheet.addRule("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        styleSheet.addRule("th { background-color: #f2f2f2; }");
        
        // 自动滚动到底部
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        // 输入框（字体大小将从配置中读取）
        inputField = new JTextField();
        inputField.setFont(new Font("Microsoft YaHei", Font.PLAIN, fontSize));
        
        // 按钮
        sendButton = new JButton("发送");
        clearButton = new JButton("清空对话");
        saveButton = new JButton("保存");
        loadButton = new JButton("加载");
        configButton = new JButton("设置");
        exportTextButton = new JButton("导出TXT");
        copyButton = new JButton("复制回复");
        clearInputButton = new JButton("清空输入");
        fontSizeLabel = new JLabel("字体大小:");
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(fontSize, 10, 24, 1));
        statusLabel = new JLabel("就绪");
        
        // 设置按钮样式
        Font buttonFont = new Font("Microsoft YaHei", Font.PLAIN, 12);
        sendButton.setFont(buttonFont);
        clearButton.setFont(buttonFont);
        saveButton.setFont(buttonFont);
        loadButton.setFont(buttonFont);
        configButton.setFont(buttonFont);
        exportTextButton.setFont(buttonFont);
        copyButton.setFont(buttonFont);
        clearInputButton.setFont(buttonFont);
        fontSizeLabel.setFont(buttonFont);
        fontSizeSpinner.setFont(buttonFont);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        
        // 应用字体大小
        applyFontSize(config.getFontSize());
    }
    
    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolBar.add(clearButton);
        toolBar.add(saveButton);
        toolBar.add(loadButton);
        toolBar.add(exportTextButton);
        toolBar.add(copyButton);
        toolBar.add(configButton);
        toolBar.add(fontSizeLabel);
        toolBar.add(fontSizeSpinner);
        
        // 聊天区域（带滚动条）
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createTitledBorder("对话记录"));
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入消息"));
        inputPanel.add(inputField, BorderLayout.CENTER);
        
        JPanel inputRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        inputRight.add(clearInputButton);
        inputRight.add(sendButton);
        inputPanel.add(inputRight, BorderLayout.EAST);
        
        // 添加到主窗口
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.CENTER);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusPanel.add(statusLabel);
        southPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(southPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 设置事件监听
     */
    private void setupEvents() {
        // 发送按钮
        sendButton.addActionListener(e -> sendMessage());
        
        // 输入框回车
        inputField.addActionListener(e -> sendMessage());
        
        // 清空按钮
        clearButton.addActionListener(e -> clearChat());
        clearInputButton.addActionListener(e -> inputField.setText(""));
        
        // 保存按钮
        saveButton.addActionListener(e -> saveConversation());
        
        // 加载按钮
        loadButton.addActionListener(e -> loadConversation());
        
        // 设置按钮
        configButton.addActionListener(e -> showConfigDialog());
        
        // 导出按钮
        exportTextButton.addActionListener(e -> exportText());
        
        // 复制回复
        copyButton.addActionListener(e -> copyLastAssistantMessage());
        
        // 字体大小调节
        fontSizeSpinner.addChangeListener(e -> {
            int newSize = (Integer) fontSizeSpinner.getValue();
            config.setFontSize(newSize);
            applyFontSize(newSize);
            try {
                config.saveToFile();
            } catch (Exception ignored) {
                // ignore persist error for font size
            }
        });
    }
    
    /**
     * 发送消息
     */
    private void sendMessage() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) {
            return;
        }
        
        // 检查API配置
        if (config.getApiKey() == null || config.getApiKey().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "请先配置API密钥！\n点击\"设置\"按钮进行配置。", 
                "配置错误", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 显示用户消息
        appendUserMessage(userInput);
        
        // ⚠️ 关键修复：先添加用户消息到历史，再发送请求
        // 确保API调用时能正确获取完整的对话上下文
        history.addMessage(new Message("user", userInput));
        
        // 清空输入框（防止重复发送）
        inputField.setText("");
        
        // 禁用输入
        sendButton.setEnabled(false);
        inputField.setEnabled(false);
        setStatus("发送中...");
        
        // 保存当前用户输入（用于API调用）
        final String currentUserMessage = userInput;
        
        // 异步调用API
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // 传入历史消息和当前用户消息
                // 注意：此时history已经包含当前用户消息
                return apiClient.sendMessage(history.getMessages(), currentUserMessage);
            }
            
            @Override
            protected void done() {
                try {
                    String response = get();
                    if (response != null && !response.trim().isEmpty()) {
                        appendAssistantMessage(response);
                        history.addMessage(new Message("assistant", response));
                    } else {
                        appendErrorMessage("错误: AI返回空响应");
                    }
                } catch (Exception ex) {
                    appendErrorMessage("错误: " + ex.getMessage());
                    // 如果API调用失败，从历史中移除刚才添加的用户消息
                    // 避免历史记录不一致
                    java.util.List<Message> msgs = history.getMessages();
                    if (!msgs.isEmpty() && msgs.get(msgs.size() - 1).getRole().equals("user")) {
                        msgs.remove(msgs.size() - 1);
                    }
                } finally {
                    sendButton.setEnabled(true);
                    inputField.setEnabled(true);
                    setStatus("就绪");
                    inputField.requestFocus();
                }
            }
        };
        
        // 执行异步任务
        worker.execute();
    }
    
    /**
     * 清空对话
     */
    private void clearChat() {
        int result = JOptionPane.showConfirmDialog(this, 
            "确定要清空当前对话吗？", 
            "确认", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                HTMLEditorKit kit = (HTMLEditorKit) chatArea.getEditorKit();
                HTMLDocument doc = (HTMLDocument) chatArea.getDocument();
                doc.remove(0, doc.getLength());
            } catch (Exception e) {
                chatArea.setText("");
            }
            history.clear();
            appendSystemMessage("对话已清空");
        }
    }
    
    /**
     * 保存对话
     */
    private void saveConversation() {
        if (history.size() == 0) {
            JOptionPane.showMessageDialog(this, 
                "当前没有对话记录可保存", 
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存对话");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "对话文件 (*.chat)", "chat"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filename = file.getAbsolutePath();
            if (!filename.endsWith(".chat")) {
                filename += ".chat";
            }
            
            try {
                history.saveToFile(filename);
                JOptionPane.showMessageDialog(this, 
                    "对话已保存到: " + filename, 
                    "成功", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "保存失败: " + e.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 加载对话
     */
    private void loadConversation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("加载对话");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "对话文件 (*.chat)", "chat"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try {
                ConversationHistory loaded = ConversationHistory.loadFromFile(file.getAbsolutePath());
                history = loaded;
                
                // 显示历史记录
                try {
                    HTMLEditorKit kit = (HTMLEditorKit) chatArea.getEditorKit();
                    HTMLDocument doc = (HTMLDocument) chatArea.getDocument();
                    doc.remove(0, doc.getLength());
                } catch (Exception e) {
                    chatArea.setText("");
                }
                for (Message msg : history.getMessages()) {
                    if ("user".equals(msg.getRole())) {
                        appendUserMessage(msg.getContent());
                    } else if ("assistant".equals(msg.getRole())) {
                        appendAssistantMessage(msg.getContent());
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "对话已加载，共 " + history.size() + " 条消息", 
                    "成功", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "加载失败: " + e.getMessage(), 
                    "错误", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 导出文本
     */
    private void exportText() {
        if (history.size() == 0) {
            JOptionPane.showMessageDialog(this,
                    "当前没有对话记录可导出",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出为TXT");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "文本文件 (*.txt)", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String filename = file.getAbsolutePath();
            if (!filename.endsWith(".txt")) {
                filename += ".txt";
            }
            
            try {
                history.exportToText(filename);
                JOptionPane.showMessageDialog(this,
                        "已导出到: " + filename,
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "导出失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * 复制最后一条AI回复
     */
    private void copyLastAssistantMessage() {
        java.util.List<Message> messages = history.getMessages();
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if ("assistant".equals(msg.getRole())) {
                StringSelection selection = new StringSelection(msg.getContent());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
                JOptionPane.showMessageDialog(this, "已复制最后一条回复", "复制成功",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "暂无AI回复可复制", "提示",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 设置状态栏文本
     */
    private void setStatus(String text) {
        statusLabel.setText(text);
    }
    
    /**
     * 应用字体大小
     * @param fontSize 字体大小（px）
     */
    private void applyFontSize(int fontSize) {
        // 更新输入框字体
        inputField.setFont(new Font("Microsoft YaHei", Font.PLAIN, fontSize));
        
        // 更新HTML样式中的字体大小
        if (chatArea.getEditorKit() instanceof HTMLEditorKit) {
            StyleSheet styleSheet = ((HTMLDocument) chatArea.getDocument()).getStyleSheet();
            
            // 更新body字体大小
            styleSheet.addRule("body { font-family: 'Microsoft YaHei', sans-serif; font-size: " + fontSize + "px; margin: 5px; " +
                              "background-color: #fafafa; color: #333333; }");
            
            // 更新标题字体大小（相对于body字体）
            int h1Size = (int)(fontSize * 1.8);
            int h2Size = (int)(fontSize * 1.5);
            int h3Size = (int)(fontSize * 1.3);
            int h4Size = (int)(fontSize * 1.1);
            
            styleSheet.addRule("h1 { font-size: " + h1Size + "px; margin-top: 10px; margin-bottom: 5px; }");
            styleSheet.addRule("h2 { font-size: " + h2Size + "px; margin-top: 10px; margin-bottom: 5px; }");
            styleSheet.addRule("h3 { font-size: " + h3Size + "px; margin-top: 10px; margin-bottom: 5px; }");
            styleSheet.addRule("h4 { font-size: " + h4Size + "px; margin-top: 10px; margin-bottom: 5px; }");
            
            // 保持其他样式规则
            styleSheet.addRule("code { background-color: #f4f4f4; padding: 2px 4px; border-radius: 3px; " +
                              "font-family: 'Consolas', 'Monaco', monospace; font-size: " + (fontSize - 1) + "px; }");
            styleSheet.addRule("pre { background-color: #f4f4f4; padding: 10px; border-radius: 5px; " +
                              "overflow-x: auto; border: 1px solid #ddd; }");
            styleSheet.addRule("pre code { background-color: transparent; padding: 0; font-size: " + (fontSize - 1) + "px; }");
            styleSheet.addRule("ul, ol { margin-left: 20px; }");
            styleSheet.addRule("li { margin: 3px 0; }");
            styleSheet.addRule("a { color: #0066cc; text-decoration: none; }");
            styleSheet.addRule("a:hover { text-decoration: underline; }");
            styleSheet.addRule("blockquote { border-left: 4px solid #ccc; margin-left: 0; " +
                              "padding-left: 15px; color: #666; }");
            styleSheet.addRule("table { border-collapse: collapse; width: 100%; margin: 10px 0; }");
            styleSheet.addRule("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            styleSheet.addRule("th { background-color: #f2f2f2; }");
            
            // 刷新显示
            chatArea.repaint();
        }
    }
    
    /**
     * 显示配置对话框
     */
    private void showConfigDialog() {
        ConfigDialog dialog = new ConfigDialog(this, config);
        dialog.setVisible(true);
        
        if (dialog.isSaved()) {
            config = dialog.getConfig();
            apiClient.updateConfig(config);
            fontSizeSpinner.setValue(config.getFontSize());
            applyFontSize(config.getFontSize());
            appendSystemMessage("配置已更新");
        }
    }
    
    /**
     * 添加用户消息
     */
    private void appendUserMessage(String message) {
        appendMessage("用户", message, new Color(0, 100, 200));
    }
    
    /**
     * 添加AI回复
     */
    private void appendAssistantMessage(String message) {
        appendMessage("AI助手", message, new Color(0, 150, 0));
    }
    
    /**
     * 添加系统消息
     */
    private void appendSystemMessage(String message) {
        appendMessage("系统", message, new Color(150, 150, 150));
    }
    
    /**
     * 添加错误消息
     */
    private void appendErrorMessage(String message) {
        appendMessage("错误", message, new Color(200, 0, 0));
    }
    
    /**
     * 添加消息到聊天区域
     */
    private void appendMessage(String role, String content, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                String htmlContent;
                // 对于AI助手的消息，使用markdown渲染；其他消息使用纯文本（转换为HTML）
                if ("AI助手".equals(role)) {
                    htmlContent = markdownToHtml(content);
                } else {
                    // 将纯文本转换为HTML，转义HTML特殊字符并保留换行
                    htmlContent = escapeHtml(content).replace("\n", "<br/>");
                }
                
                // 构建HTML消息
                String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
                String messageHtml = String.format(
                    "<div style='margin-bottom: 15px;'>" +
                    "<span style='color: %s; font-weight: bold;'>[%s]</span><br/>" +
                    "%s" +
                    "</div>",
                    hexColor, role, htmlContent
                );
                
                // 插入HTML内容
                HTMLEditorKit kit = (HTMLEditorKit) chatArea.getEditorKit();
                HTMLDocument doc = (HTMLDocument) chatArea.getDocument();
                kit.insertHTML(doc, doc.getLength(), messageHtml, 0, 0, null);
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            } catch (Exception e) {
                // 如果HTML插入失败，使用纯文本方式
                try {
                    String plainText = "[" + role + "] " + content + "\n\n";
                    Document doc = chatArea.getDocument();
                    doc.insertString(doc.getLength(), plainText, null);
                    chatArea.setCaretPosition(doc.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
    
    /**
     * 将Markdown转换为HTML
     * 支持基本的Markdown语法：代码块、行内代码、粗体、斜体、标题、列表等
     */
    private String markdownToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        
        StringBuilder html = new StringBuilder();
        String[] lines = markdown.split("\n");
        boolean inCodeBlock = false;
        boolean inList = false;
        String listType = "";
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            // 处理代码块
            if (line.trim().startsWith("```")) {
                if (inCodeBlock) {
                    html.append("</code></pre>");
                    inCodeBlock = false;
                } else {
                    String lang = line.substring(3).trim();
                    html.append("<pre><code>");
                    inCodeBlock = true;
                }
                continue;
            }
            
            if (inCodeBlock) {
                html.append(escapeHtml(line)).append("\n");
                continue;
            }
            
            // 关闭列表
            if (inList && !isListItem(line) && !line.trim().isEmpty()) {
                html.append(listType.equals("ol") ? "</ol>" : "</ul>");
                inList = false;
            }
            
            // 处理标题
            if (line.trim().startsWith("# ")) {
                html.append("<h1>").append(processInlineMarkdown(line.substring(2))).append("</h1>");
                continue;
            } else if (line.trim().startsWith("## ")) {
                html.append("<h2>").append(processInlineMarkdown(line.substring(3))).append("</h2>");
                continue;
            } else if (line.trim().startsWith("### ")) {
                html.append("<h3>").append(processInlineMarkdown(line.substring(4))).append("</h3>");
                continue;
            } else if (line.trim().startsWith("#### ")) {
                html.append("<h4>").append(processInlineMarkdown(line.substring(5))).append("</h4>");
                continue;
            }
            
            // 处理有序列表
            if (line.matches("^\\s*\\d+\\.\\s+.*")) {
                if (!inList || !"ol".equals(listType)) {
                    if (inList) html.append("</ul>");
                    html.append("<ol>");
                    inList = true;
                    listType = "ol";
                }
                String item = line.replaceFirst("^\\s*\\d+\\.\\s+", "");
                html.append("<li>").append(processInlineMarkdown(item)).append("</li>");
                continue;
            }
            
            // 处理无序列表
            if (line.matches("^\\s*[-*+]\\s+.*")) {
                if (!inList || !"ul".equals(listType)) {
                    if (inList) html.append("</ol>");
                    html.append("<ul>");
                    inList = true;
                    listType = "ul";
                }
                String item = line.replaceFirst("^\\s*[-*+]\\s+", "");
                html.append("<li>").append(processInlineMarkdown(item)).append("</li>");
                continue;
            }
            
            // 处理空行
            if (line.trim().isEmpty()) {
                html.append("<br/>");
                continue;
            }
            
            // 处理普通段落
            html.append("<p>").append(processInlineMarkdown(line)).append("</p>");
        }
        
        // 关闭未闭合的代码块和列表
        if (inCodeBlock) {
            html.append("</code></pre>");
        }
        if (inList) {
            html.append(listType.equals("ol") ? "</ol>" : "</ul>");
        }
        
        return html.toString();
    }
    
    /**
     * 处理行内Markdown（粗体、斜体、行内代码、链接等）
     */
    private String processInlineMarkdown(String text) {
        if (text == null) return "";
        
        // 先处理行内代码（使用临时标记避免后续处理影响代码内容）
        java.util.List<String> codeBlocks = new java.util.ArrayList<>();
        int codeIndex = 0;
        String codePattern = "`([^`]+)`";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(codePattern);
        java.util.regex.Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String codeContent = matcher.group(1);
            // 转义代码内容中的HTML
            codeContent = escapeHtml(codeContent);
            codeBlocks.add(codeContent);
            matcher.appendReplacement(sb, "___CODE_" + codeIndex + "___");
            codeIndex++;
        }
        matcher.appendTail(sb);
        text = sb.toString();
        
        // 转义HTML特殊字符（代码已被替换，不受影响）
        text = escapeHtml(text);
        
        // 恢复代码块
        for (int i = 0; i < codeBlocks.size(); i++) {
            text = text.replace("___CODE_" + i + "___", "<code>" + codeBlocks.get(i) + "</code>");
        }
        
        // 处理粗体 **text** 或 __text__
        text = text.replaceAll("\\*\\*([^*]+)\\*\\*", "<strong>$1</strong>");
        text = text.replaceAll("__([^_]+)__", "<strong>$1</strong>");
        
        // 处理斜体 *text* 或 _text_（但不能是粗体的开始，也不能是代码标记的一部分）
        text = text.replaceAll("(?<!\\*|`)\\*([^*`]+)\\*(?!\\*|`)", "<em>$1</em>");
        text = text.replaceAll("(?<!_|`)_([^_`]+)_(?!_|`)", "<em>$1</em>");
        
        // 处理链接 [text](url)（链接中不能包含代码）
        text = text.replaceAll("(?<!`)\\[([^\\]]+)\\]\\(([^\\)]+)\\)(?!`)", "<a href=\"$2\">$1</a>");
        
        return text;
    }
    
    /**
     * 转义HTML特殊字符
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    /**
     * 判断是否为列表项
     */
    private boolean isListItem(String line) {
        return line.matches("^\\s*\\d+\\.\\s+.*") || line.matches("^\\s*[-*+]\\s+.*");
    }
}


