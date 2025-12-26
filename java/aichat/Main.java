package aichat;

import javax.swing.*;

/**
 * 主程序入口类
 * 启动AI智能对话助手应用程序
 */
public class Main {
    /**
     * 主方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 在EDT线程中启动GUI
        SwingUtilities.invokeLater(() -> {
            try {
                // 设置系统外观
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("设置外观失败: " + e.getMessage());
            }
            
            // 创建临时窗口用于登录对话框的父窗口
            JFrame tempFrame = new JFrame();
            tempFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // 显示登录对话框
            LoginDialog loginDialog = new LoginDialog(tempFrame);
            loginDialog.setVisible(true);
            
            // 检查登录是否成功
            if (loginDialog.isLoginSuccess()) {
                // 关闭临时窗口
                tempFrame.dispose();
                
                // 创建并显示主窗口
                ChatMainFrame frame = new ChatMainFrame();
                frame.setTitle("AI智能对话助手 - 欢迎, " + loginDialog.getLoggedInUsername());
                frame.setVisible(true);
            } else {
                // 登录失败或取消，退出程序
                System.exit(0);
            }
        });
    }
}


