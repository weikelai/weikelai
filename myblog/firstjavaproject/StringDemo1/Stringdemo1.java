package com.myblog.firstjavaproject.StringDemo1;

import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-12
 * @Description: 用户登陆练习
 * @Version: 1.0
 */

public class Stringdemo1 {
    public static void main(String[] args) {
        String rightusername = "赖国燕";
        String rightnumber = "12345";

        for(int i = 0;i < 3;i++){
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入用户名：");
            String username = sc.next();
            System.out.println("请输入密码：");
            String number = sc.next();

            if(username.equals(rightusername)&&number.equals(rightnumber)){
                System.out.println("successful");
                break;
            }else{
                System.out.println("error");
            }
        }
    }
}
