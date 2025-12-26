package com.myblog.firstjavaproject.javawork.exp4.user;
import com.myblog.firstjavaproject.javawork.exp4.opera.*;
import java.util.Scanner;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 正常用户
 * @Version: 1.0
 */


public class NormalUser extends User {
    public NormalUser(String name) {
        super(name);
        this.ioPerations = new IOPeration[]{//引用,这边用super也可以，因为这里没有同名的，不需要做区分。用this最好
                new ExitOperation(),
                new FindOperation(),
                new BrrowOperation(),
                new ReturnOperation()
                //以动态方式申请内存。拿到变量后，我们就给他们分配内存
        };
    }

    @Override
    public int menu() {
        System.out.println("_________________");
        System.out.println("hello," + name + "~");
        System.out.println("1.查找图书！");
        System.out.println("2.借阅图书！");
        System.out.println("3.归还图书！");
        System.out.println("0.退出系统！");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        return choice;
    }
}