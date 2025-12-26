package com.myblog.firstjavaproject.javawork;
import java.util.Scanner;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-11-22
 * @Description: 给出两个二进制数乘法的分治法实现
 * @Version: 1.0
 */

public class BinaryKaratsuba {

    // 去掉字符串前导 0
    private static String trimZero(String s) {
        return s.replaceFirst("^0+(?!$)", "");
    }

    // 二进制字符串加法
    private static String addBinary(String a, String b) {
        StringBuilder res = new StringBuilder();
        int carry = 0;
        int i = a.length() - 1, j = b.length() - 1;

        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;
            if (i >= 0) sum += a.charAt(i--) - '0';
            if (j >= 0) sum += b.charAt(j--) - '0';

            res.append(sum % 2);
            carry = sum / 2;
        }

        return res.reverse().toString();
    }

    // 二进制字符串减法（保证 a >= b）
    private static String subBinary(String a, String b) {
        StringBuilder res = new StringBuilder();
        int borrow = 0;
        int i = a.length() - 1, j = b.length() - 1;

        while (i >= 0) {
            int diff = (a.charAt(i--) - '0') - borrow;
            if (j >= 0) diff -= (b.charAt(j--) - '0');

            if (diff < 0) {
                diff += 2;
                borrow = 1;
            } else {
                borrow = 0;
            }
            res.append(diff);
        }

        return trimZero(res.reverse().toString());
    }

    // 对齐长度，使 x,y 等长
    private static String[] align(String x, String y) {
        int len = Math.max(x.length(), y.length());
        while (x.length() < len) x = "0" + x;
        while (y.length() < len) y = "0" + y;
        return new String[]{x, y};
    }

    // 分治法二进制乘法
    public static String karatsuba(String x, String y) {
        x = trimZero(x);
        y = trimZero(y);

        // 若为一位
        if (x.length() == 1 && y.length() == 1) {
            return String.valueOf((x.charAt(0) - '0') * (y.charAt(0) - '0'));
        }

        // 统一长度
        String[] t = align(x, y);
        x = t[0];
        y = t[1];

        int n = x.length();
        int m = n / 2;

        String x1 = x.substring(0, n - m);
        String x0 = x.substring(n - m);
        String y1 = y.substring(0, n - m);
        String y0 = y.substring(n - m);

        // z2 = x1*y1
        String z2 = karatsuba(x1, y1);
        // z0 = x0*y0
        String z0 = karatsuba(x0, y0);
        // z1 = (x1+x0)*(y1+y0) - z2 - z0
        String z1 = karatsuba(addBinary(x1, x0), addBinary(y1, y0));
        z1 = subBinary(z1, z2);
        z1 = subBinary(z1, z0);

        //  result = z2 << 2m + z1 << m + z0
        for (int i = 0; i < 2 * m; i++) z2 += "0";
        for (int i = 0; i < m; i++) z1 += "0";

        return trimZero(addBinary(addBinary(z2, z1), z0));
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("请输入第一个二进制数 x：");
        String x = sc.nextLine();

        System.out.print("请输入第二个二进制数 y：");
        String y = sc.nextLine();

        String result = karatsuba(x, y);

        System.out.println("计算结果：");
        System.out.println("x × y = " + result);

        // 验证
        long vx = Long.parseLong(x, 2);
        long vy = Long.parseLong(y, 2);
        System.out.println("普通乘法验证：" + Long.toBinaryString(vx * vy));

        sc.close();
    }
}

