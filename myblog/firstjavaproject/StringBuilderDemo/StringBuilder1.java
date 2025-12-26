package com.myblog.firstjavaproject.StringBuilderDemo;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-12
 * @Description: practice
 * @Version: 1.0
 */

public class StringBuilder1 {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("123");
        sb.append("aaa");
        sb.append("ddd");
        System.out.println(sb);
        String str = sb.toString();
        System.out.println(str);
    }
}
