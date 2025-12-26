package com.myblog.firstjavaproject.javawork;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 用对象流实现一简单的图书管理系统
 * @Version: 1.0
 */

/**
 * 简易图书管理系统（对象流）
 * 功能：
 * 1. 新增图书
 * 2. 删除图书
 * 3. 查找图书
 * 4. 借阅图书
 * 5. 归还图书
 * 6. 显示全部图书
 * 7. 退出（自动保存到 books.dat）
 */
public class BookManager {

    // ======== 图书类（可序列化）========
    static class Book implements Serializable {
        private String name;
        private String author;
        private boolean isBorrowed;

        public Book(String name, String author) {
            this.name = name;
            this.author = author;
            this.isBorrowed = false;
        }

        public String getName() { return name; }
        public boolean isBorrowed() { return isBorrowed; }
        public void borrow() { isBorrowed = true; }
        public void back() { isBorrowed = false; }

        @Override
        public String toString() {
            return "书名：" + name + " | 作者：" + author + " | 状态：" + (isBorrowed ? "已借出" : "在库");
        }
    }

    // ======== 成员变量：图书列表 ========
    private List<Book> books = new ArrayList<>();
    private final String FILE_NAME = "books.dat";

    public BookManager() {
        load();
    }

    // ======== 加载数据 ========
    @SuppressWarnings("unchecked")
    private void load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("未检测到历史数据，将创建新的数据文件。");
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            books = (List<Book>) ois.readObject();
            System.out.println("已加载 " + books.size() + " 本图书。");
        } catch (Exception e) {
            System.out.println("数据加载失败，创建新数据文件。");
        }
    }

    // ======== 保存数据 ========
    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(books);
            System.out.println("数据已保存到文件 books.dat");
        } catch (Exception e) {
            System.out.println("保存失败：" + e.getMessage());
        }
    }

    // ======== 功能 ========

    // 1. 新增图书
    private void addBook() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入书名：");
        String name = sc.nextLine();
        System.out.println("请输入作者：");
        String author = sc.nextLine();

        for (Book b : books) {
            if (b.getName().equals(name)) {
                System.out.println("已存在同名图书！");
                return;
            }
        }

        books.add(new Book(name, author));
        System.out.println("图书新增成功！");
    }

    // 2. 删除图书
    private void deleteBook() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要删除的书名：");
        String name = sc.nextLine();

        for (Book b : books) {
            if (b.getName().equals(name)) {
                books.remove(b);
                System.out.println("✔ 图书已删除！");
                return;
            }
        }
        System.out.println("未找到此图书！");
    }

    // 3. 查找图书
    private void findBook() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要查找的书名：");
        String name = sc.nextLine();

        for (Book b : books) {
            if (b.getName().equals(name)) {
                System.out.println("查找到图书：");
                System.out.println(b);
                return;
            }
        }
        System.out.println("没有这本书！");
    }

    // 4. 借阅图书
    private void borrowBook() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要借阅的书名：");
        String name = sc.nextLine();

        for (Book b : books) {
            if (b.getName().equals(name)) {
                if (b.isBorrowed()) {
                    System.out.println("该书已被借出！");
                } else {
                    b.borrow();
                    System.out.println("借阅成功！");
                }
                return;
            }
        }
        System.out.println("没有这本书！");
    }

    // 5. 归还图书
    private void returnBook() {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入要归还的书名：");
        String name = sc.nextLine();

        for (Book b : books) {
            if (b.getName().equals(name)) {
                if (!b.isBorrowed()) {
                    System.out.println("该书未被借出！");
                } else {
                    b.back();
                    System.out.println("归还成功！");
                }
                return;
            }
        }
        System.out.println("没有这本书！");
    }

    // 6. 显示全部图书
    private void showAll() {
        System.out.println("\n====== 全部图书 ======");
        if (books.isEmpty()) {
            System.out.println("暂无图书！");
        }
        for (Book b : books) {
            System.out.println(b);
        }
        System.out.println("=====================\n");
    }

    // ======== 菜单与主循环 ========
    public void menu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("===== 图书管理系统 =====");
            System.out.println("1. 新增图书");
            System.out.println("2. 删除图书");
            System.out.println("3. 查找图书");
            System.out.println("4. 借阅图书");
            System.out.println("5. 归还图书");
            System.out.println("6. 显示全部图书");
            System.out.println("7. 退出系统");
            System.out.println("请输入你的选择：");

            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    deleteBook();
                    break;
                case 3:
                    findBook();
                    break;
                case 4:
                    borrowBook();
                    break;
                case 5:
                    returnBook();
                    break;
                case 6:
                    showAll();
                    break;
                case 7:
                    save();
                    System.out.println("系统已退出。");
                    return;
                default:
                    System.out.println("无效输入！");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new BookManager().menu();
    }
}
