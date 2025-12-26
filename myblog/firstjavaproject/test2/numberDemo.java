package com.myblog.firstjavaproject.test2;

import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-16
 * @Description:
 * @Version: 1.0
 */

public class numberDemo {
    public static void main(String[] args) {
        // Number 是抽象类，这里实际上赋值的是一个 Double 对象
        Scanner scan = new Scanner(System.in);
        System.out.println("next方式接收：");
        if(scan.hasNext()){
            String str1 = scan.nextLine();
            System.out.println(str1);
        }
        StringBuilder sb = new StringBuilder();
            sb.append("music");
        System.out.println(sb);
            sb.append(1);
        System.out.println(sb);
        sb.insert(2,"love");
        System.out.println(sb);
        sb.delete(1,3);
        System.out.println(sb);
        int size = 10;
        double[] a = new double[size];
        double[] mylist = {1.2,3.2,5.4,9.9};


//        Number num = 1234.56;  // 自动装箱为 Double 类型
//
//        System.out.println(num.intValue());    // 1234 (截断小数部分)
//        System.out.println(num.longValue());   // 1234
//        System.out.println(num.floatValue());  // 1234.56
//        System.out.println(num.doubleValue()); // 1234.56
    }
}

