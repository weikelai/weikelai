package com.myblog.firstjavaproject.javawork.exp4.user;
import com.myblog.firstjavaproject.javawork.exp4.opera.*;
import java.util.Scanner;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 管理员
 * @Version: 1.0
 */

public class AdminUser extends User{
    public AdminUser(String name) {
        super(name);
        this.ioPerations=new IOPeration[]{
                new ExitOperation(),
                new FindOperation(),
                new AddOperation(),
                new DelOperation(),
                new ShowOperation()
        };
    }
    @Override
    public int menu() {//因为返回值choice是int类型的
        System.out.println("____________________________________");
        System.out.println("1.查找图书");
        System.out.println("2.新增图书");
        System.out.println("3.删除图书");
        System.out.println("4.显示图书");
        System.out.println("0.退出系统");
        System.out.println("请选择你需要的功能：");
        Scanner scanner=new Scanner(System.in);
        int choice=scanner.nextInt();
        return choice;
    }

}