package com.myblog.firstjavaproject.javawork;
import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-11-15
 * @Description: 在主窗口画一个自己设计的二维图形
 * @Version: 1.0
 */
/**
 * 示例2.2 主程序
 */
public class PaintDemo extends JFrame {

    public PaintDemo() {
        super("画图 Demo");
    }

    public static void main(String[] args) {
        PaintDemo frame = new PaintDemo();
        Scanner input = new Scanner(System.in);

        System.out.print("请输入绘制矩形的左上顶点坐标(x y)：");
        int ox = input.nextInt();
        int oy = input.nextInt();

        System.out.print("请输入矩形的宽和高：");
        int w = input.nextInt();
        int h = input.nextInt();

        Point origin = new Point(ox, oy);
        RectangleD rd = new RectangleD(origin, w, h);

        frame.add(rd);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);   // 居中
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
