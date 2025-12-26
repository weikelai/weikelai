package aichat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 登录对话框类
 * 提供用户登录界面，支持登录和注册功能
 */
public class LoginDialog extends JDialog {
    private UserManager userManager;
    private boolean loginSuccess = false;
    private String loggedInUsername;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    
    /**
     * 构造方法
     * @param parent 父窗口
     */
    public LoginDialog(JFrame parent) {
        super(parent, "用户登录", true);
        this.userManager = new UserManager();
        
        initComponents();
        setupLayout();
        setupEvents();
        
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("登录");
        registerButton = new JButton("注册");
        statusLabel = new JLabel("请输入用户名和密码");
        
        // 设置字体
        Font font = new Font("Microsoft YaHei", Font.PLAIN, 12);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        registerButton.setFont(font);
        statusLabel.setFont(font);
        
        statusLabel.setForeground(new Color(100, 100, 100));
    }
    
    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // 主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 标题
        JLabel titleLabel = new JLabel("AI智能对话助手");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(titleLabel, gbc);
        
        // 用户名
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(usernameField, gbc);
        
        // 密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(passwordField, gbc);
        
        // 状态标签
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(statusLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 提示信息
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel infoLabel = new JLabel("<html><center>默认账号: admin/admin123 或 user/user123<br/>或 test/test123</center></html>");
        infoLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
        infoLabel.setForeground(new Color(150, 150, 150));
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.NORTH);
    }
    
    /**
     * 设置事件监听
     */
    private void setupEvents() {
        // 登录按钮
        loginButton.addActionListener(e -> performLogin());
        
        // 注册按钮
        registerButton.addActionListener(e -> performRegister());
        
        // 回车键登录
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        usernameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    /**
     * 执行登录
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("请输入用户名和密码", Color.RED);
            return;
        }
        
        if (userManager.validateUser(username, password)) {
            loginSuccess = true;
            loggedInUsername = username;
            showStatus("登录成功！", new Color(0, 150, 0));
            
            // 延迟关闭对话框，让用户看到成功消息
            Timer timer = new Timer(500, e -> dispose());
            timer.setRepeats(false);
            timer.start();
        } else {
            showStatus("用户名或密码错误！", Color.RED);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
    
    /**
     * 执行注册
     */
    private void performRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("请输入用户名和密码", Color.RED);
            return;
        }
        
        if (password.length() < 3) {
            showStatus("密码长度至少3位！", Color.RED);
            return;
        }
        
        if (userManager.userExists(username)) {
            showStatus("用户名已存在！", Color.RED);
            return;
        }
        
        if (userManager.registerUser(username, password)) {
            showStatus("注册成功！请登录", new Color(0, 150, 0));
            passwordField.setText("");
        } else {
            showStatus("注册失败！", Color.RED);
        }
    }
    
    /**
     * 显示状态信息
     */
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    /**
     * 是否登录成功
     * @return 是否成功
     */
    public boolean isLoginSuccess() {
        return loginSuccess;
    }
    
    /**
     * 获取登录的用户名
     * @return 用户名
     */
    public String getLoggedInUsername() {
        return loggedInUsername;
    }
}

