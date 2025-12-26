package com.myblog.firstjavaproject.javawork.exp5;
import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-06
 * @Description: 在主窗口画一个自己设计的二维图形加入自定义的异常处理功能
 * @Version: 1.0
 */

public class exp5_PaintDemo extends JFrame {

    public exp5_PaintDemo() {
        super("画图 Demo（含异常处理）");
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        while (true) {

            System.out.println("\n=== 绘制矩形（按 Enter 直接结束程序） ===");
            System.out.print("请输入左上角坐标 x y：");

            // 如果用户只按 Enter ⇒ exit
            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("程序结束，再见！");
                break;
            }

            try {
                // 解析 x y
                String[] xy = line.split("\\s+");
                if (xy.length != 2) throw new RectangleException("请输入两个整数作为坐标！");

                int ox = Integer.parseInt(xy[0]);
                int oy = Integer.parseInt(xy[1]);

                System.out.print("请输入矩形宽和高 w h：");
                line = input.nextLine().trim();
                if (line.isEmpty()) throw new RectangleException("宽和高不能为空！");

                String[] wh = line.split("\\s+");
                if (wh.length != 2) throw new RectangleException("请输入两个整数作为宽和高！");

                int w = Integer.parseInt(wh[0]);
                int h = Integer.parseInt(wh[1]);

                // 创建窗口
                exp5_PaintDemo frame = new exp5_PaintDemo();
                RectangleD rd = new RectangleD(new Point(ox, oy), w, h);

                frame.add(rd);
                frame.setSize(400, 400);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

            } catch (RectangleException e) {
                System.out.println("自定义异常：" + e.getMessage());
                System.out.println("请重新输入！");
            } catch (NumberFormatException e) {
                System.out.println("输入格式错误：请输入整数！");
            } catch (Exception e) {
                System.out.println("系统异常：" + e.getMessage());
            }
        }

        input.close();
    }
}
