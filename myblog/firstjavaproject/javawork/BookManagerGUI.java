package com.myblog.firstjavaproject.javawork;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-20
 * @Description: 实验四——修改项目4，实现图形界面的图书管理系统
 * @Version: 1.0
 */
public class BookManagerGUI extends JFrame {

    /**
     * 内部 Book 类
     */
    static class Book {
        String name;
        String author;
        boolean borrowed;
    }

    private JTextField nameField, authorField;
    private JTable table;
    private DefaultTableModel model;

    public BookManagerGUI() {
        initUI();
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        setTitle("图书管理系统");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /* 顶部输入区 */
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("图书信息"));
        topPanel.add(new JLabel("书名："));
        nameField = new JTextField();
        topPanel.add(nameField);
        topPanel.add(new JLabel("作者："));
        authorField = new JTextField();
        topPanel.add(authorField);

        /* 表格区 */
        model = new DefaultTableModel(new String[] { "书名", "作者", "状态" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可直接编辑
            }
        };
        table = new JTable(model);
        refreshTable();

        /* 按钮区 */
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("新增");
        JButton delBtn = new JButton("删除");
        JButton borrowBtn = new JButton("借阅");
        JButton returnBtn = new JButton("归还");
        JButton exitBtn = new JButton("退出");

        btnPanel.add(addBtn);
        btnPanel.add(delBtn);
        btnPanel.add(borrowBtn);
        btnPanel.add(returnBtn);
        btnPanel.add(exitBtn);

        /* 事件绑定 */
        addBtn.addActionListener(e -> addBook());
        delBtn.addActionListener(e -> deleteBook());
        borrowBtn.addActionListener(e -> borrowBook());
        returnBtn.addActionListener(e -> returnBook());
        exitBtn.addActionListener(e -> System.exit(0));

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * 刷新表格
     */
    private void refreshTable() {
        model.setRowCount(0);
        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM book";
            java.sql.Statement stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getBoolean("borrowed") ? "已借出" : "在库"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增图书
     */
    private void addBook() {
        String name = nameField.getText().trim();
        String author = authorField.getText().trim();
        if (name.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "书名和作者不能为空！");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO book(name, author, borrowed) VALUES (?, ?, false)";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, author);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        nameField.setText("");
        authorField.setText("");
        refreshTable();
    }

    /**
     * 删除图书
     */
    private void deleteBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的图书！");
            return;
        }

        String name = model.getValueAt(row, 0).toString();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "DELETE FROM book WHERE name = ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshTable();
    }

    /**
     * 借阅图书
     */
    private void borrowBook() {
        updateBorrowStatus(true);
    }

    /**
     * 归还图书
     */
    private void returnBook() {
        updateBorrowStatus(false);
    }

    private void updateBorrowStatus(boolean status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择图书！");
            return;
        }

        String name = model.getValueAt(row, 0).toString();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE book SET borrowed=? WHERE name=?";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, status);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        refreshTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookManagerGUI::new);
    }
}
