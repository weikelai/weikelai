package com.myblog.firstjavaproject.javawork.exp4;
import com.myblog.firstjavaproject.javawork.exp4.book.BookList;
import com.myblog.firstjavaproject.javawork.exp4.book.Book;
import java.util.Scanner;
import com.myblog.firstjavaproject.javawork.exp4.user.AdminUser;
import com.myblog.firstjavaproject.javawork.exp4.user.NormalUser;
import com.myblog.firstjavaproject.javawork.exp4.user.User;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 主函数
 * @Version: 1.0
 */

public class Librarybook {
    //登录
    public static User login() {
        System.out.println("请输入你的姓名：");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        System.out.println("请选择你的身份：1->管理员 0->普通用户");
        int choice = scanner.nextInt();
        if (choice == 1) {
            //说明是管理员
            //由于有返回值,所以我们的方法返回值就不能写void了。但是我们也无法确定返回值是什么,可能是管理员,可能是用户。所以,用向上转型,写User.
            return new AdminUser(name);//返回实例化一个管理员对象
        } else {
            return new NormalUser(name);//返回实例化一个用户对象
        }
    }

    public static void main(String[] args) {
        BookList bookList = new BookList();  // 只创建一次，所有用户共享同一套书籍

        while (true) {   // 外层循环：允许用户反复重新登录
            User user = login();      // 每次登录一个新用户
            System.out.println("登录成功，欢迎：" + user.getName());

            boolean isRunning = true;

            while (isRunning) {   // 内层循环：当前用户在操作
                int choice = user.menu();
                isRunning = user.doWork(choice, bookList);
                // doWork 返回 false → 当前用户退出 → 跳出内层循环 → 回到登录界面
            }
            System.out.println("用户已退出，返回登录界面...\n");
        }
    }

}