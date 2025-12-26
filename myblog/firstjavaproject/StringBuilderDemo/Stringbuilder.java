package com.myblog.firstjavaproject.StringBuilderDemo;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-12
 * @Description:
 * @Version: 1.0
 */

public class Stringbuilder {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder("abc");
        sb.append(123);
        sb.append(true);
        System.out.println(sb);
        sb.reverse();
        System.out.println(sb);
        int len = sb.length();
        System.out.println(len);
    }
}
