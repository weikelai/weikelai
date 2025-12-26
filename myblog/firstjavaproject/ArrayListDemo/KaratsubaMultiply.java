package com.myblog.firstjavaproject.ArrayListDemo;

import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-10-10
 * @Description: 使用分治法（Karatsuba算法）实现两个整数的乘法
 * @Version: 1.0
 */

public class KaratsubaMultiply {
    // Karatsuba 递归乘法
    public static long karatsuba(long x, long y) {
        // 基本情况：当数字较小时直接返回
        if (x < 10 || y < 10) return x * y;

        // 计算长度
        int n = Math.max(Long.toString(x).length(), Long.toString(y).length());
        int m = n / 2;

        // 分解 x, y
        long highX = x / (long) Math.pow(10, m);
        long lowX  = x % (long) Math.pow(10, m);
        long highY = y / (long) Math.pow(10, m);
        long lowY  = y % (long) Math.pow(10, m);

        // 三次递归
        long z2 = karatsuba(highX, highY);
        long z0 = karatsuba(lowX, lowY);
        long z1 = karatsuba(highX + lowX, highY + lowY) - z2 - z0;

        // 合并结果
        return z2 * (long) Math.pow(10, 2 * m) + z1 * (long) Math.pow(10, m) + z0;
    }
//    public static void main(String[] args) {
//        long x = 1234;
//        long y = 5678;
//        long result = karatsuba(x, y);
//        System.out.println("x = " + x + ", y = " + y);
//        System.out.println("乘积结果：" + result);
//        System.out.println("验证：" + (x * y));
//    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入第一个整数 x：");
        long x = sc.nextLong();
        System.out.print("请输入第二个整数 y：");
        long y = sc.nextLong();

        long result = karatsuba(x, y);

        System.out.println("乘积结果："+"x * y = "+ result);
        System.out.println("普通乘法验证：" + (x * y));

        sc.close();
    }
}
