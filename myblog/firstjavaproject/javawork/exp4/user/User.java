package com.myblog.firstjavaproject.javawork.exp4.user;
import com.myblog.firstjavaproject.javawork.exp4.opera.IOPeration;
import com.myblog.firstjavaproject.javawork.exp4.book.BookList;
import com.myblog.firstjavaproject.javawork.exp4.book.Book;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description:
 * @Version: 1.0
 */
public abstract class User {//抽象类
    protected String name;//名字.这边的protect代表的是名字的权限。如果是private,它只能在同一个包的同一类使用。就不能让AdminUser类继承了。写public的话
    //权限太大了，不是很好。
    protected IOPeration[] ioPerations;
    public User(String name) {//构造方法
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public abstract int menu();//抽象方法,打印菜单,因为有了choice返回值int类型，所以void改成int
    public boolean doWork(int choice, BookList bookList) {
        if (choice == 0) {
            System.out.println("用户已退出当前账号！");
            return false;   // 通知 main() 退出当前用户循环
        }

        this.ioPerations[choice].work(bookList);
        return true;        // 继续当前用户会话
    }
}
