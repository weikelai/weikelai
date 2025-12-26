package com.myblog.firstjavaproject.javawork;

import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-11-29
 * @Description: 用户输入两个立方体边长并比较大小，按回车退出
 */

// 定义接口
interface Relatable {
    int isLargerThan(Relatable other);
}

// CubicPlus类
class CubicPlus implements Relatable {
    private double side;

    public CubicPlus(double side) {
        this.side = side;
    }

    public double getVolume() {
        return side * side * side;
    }

    @Override
    public int isLargerThan(Relatable other) {
        if (other instanceof CubicPlus) {
            CubicPlus otherCubic = (CubicPlus) other;
            double thisVol = this.getVolume();
            double otherVol = otherCubic.getVolume();
            if (thisVol > otherVol) return 1;
            else if (thisVol < otherVol) return -1;
            else return 0;
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("立方体[边长=%.2f, 体积=%.2f]", side, getVolume());
    }
}

// 主类
public class CubicPlusTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== 立方体大小比较程序（按 Enter 直接退出） ===");

        while (true) {
            System.out.print("请输入立方体1的边长（或按 Enter 退出）：");
            String input1 = scanner.nextLine().trim();
            if (input1.isEmpty()) break;

            System.out.print("请输入立方体2的边长（或按 Enter 退出）：");
            String input2 = scanner.nextLine().trim();
            if (input2.isEmpty()) break;

            double s1, s2;

            try {
                s1 = Double.parseDouble(input1);
                s2 = Double.parseDouble(input2);
            } catch (NumberFormatException e) {
                System.out.println("输入无效，请输入数字。");
                continue;
            }

            CubicPlus c1 = new CubicPlus(s1);
            CubicPlus c2 = new CubicPlus(s2);

            System.out.println("立方体1: " + c1);
            System.out.println("立方体2: " + c2);

            int result = c1.isLargerThan(c2);
            System.out.print("比较结果：");
            if (result > 0) System.out.println("立方体1 大于 立方体2");
            else if (result < 0) System.out.println("立方体1 小于 立方体2");
            else System.out.println("立方体1 等于 立方体2");
        }

        System.out.println("程序已退出。");
        scanner.close();
    }
}
