package aichat;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 用户管理类
 * 负责用户注册、登录验证等功能
 */
public class UserManager {
    private static final String USER_FILE = "users.properties";
    private Map<String, String> users; // 用户名 -> 密码
    
    /**
     * 构造方法
     */
    public UserManager() {
        this.users = new HashMap<>();
        loadUsers();
        // 如果没有用户文件，创建默认测试账号
        if (users.isEmpty()) {
            createDefaultUsers();
        }
    }
    
    /**
     * 从文件加载用户信息
     */
    private void loadUsers() {
        File userFile = new File(USER_FILE);
        if (!userFile.exists()) {
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(userFile);
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8")) {
            
            Properties props = new Properties();
            props.load(isr);
            
            for (String username : props.stringPropertyNames()) {
                users.put(username, props.getProperty(username));
            }
            
        } catch (IOException e) {
            System.err.println("加载用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存用户信息到文件
     */
    private void saveUsers() {
        try (FileOutputStream fos = new FileOutputStream(USER_FILE);
             OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")) {
            
            Properties props = new Properties();
            for (Map.Entry<String, String> entry : users.entrySet()) {
                props.setProperty(entry.getKey(), entry.getValue());
            }
            props.store(osw, "User Accounts");
            
        } catch (IOException e) {
            System.err.println("保存用户信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建默认测试账号
     */
    private void createDefaultUsers() {
        // 默认账号：admin/admin123
        users.put("admin", "admin123");
        // 默认账号：user/user123
        users.put("user", "user123");
        // 默认账号：test/test123
        users.put("test", "test123");
        saveUsers();
    }
    
    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return 验证是否成功
     */
    public boolean validateUser(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        
        String storedPassword = users.get(username.trim());
        return storedPassword != null && storedPassword.equals(password);
    }
    
    /**
     * 注册新用户
     * @param username 用户名
     * @param password 密码
     * @return 注册是否成功（如果用户名已存在则返回false）
     */
    public boolean registerUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            return false;
        }
        
        String trimmedUsername = username.trim();
        if (users.containsKey(trimmedUsername)) {
            return false; // 用户名已存在
        }
        
        users.put(trimmedUsername, password);
        saveUsers();
        return true;
    }
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean userExists(String username) {
        return username != null && users.containsKey(username.trim());
    }
    
    /**
     * 修改密码
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改是否成功
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (!validateUser(username, oldPassword)) {
            return false;
        }
        
        users.put(username.trim(), newPassword);
        saveUsers();
        return true;
    }
}

