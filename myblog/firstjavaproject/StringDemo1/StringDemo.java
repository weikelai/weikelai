package com.myblog.firstjavaproject.StringDemo1;

import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-12
 * @Description: 字符串练习
 * @Version: 1.0
 */

public class StringDemo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入一个字符串");
        String str1 = sc.next();
        String str2 = "abc";
        System.out.println(str1 == str2);
    }
}
