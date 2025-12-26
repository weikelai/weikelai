package com.myblog.firstjavaproject.javawork;

import java.awt.*;
import javax.swing.JPanel;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-11-15
 * @Description: 矩形类
 * @Version: 1.0
 */

public class RectangleD extends JPanel {
    private int width = 0;
    private int height = 0;
    public Point origin;

    // 默认构造函数：创建一个原点在(0,0)的矩形
    public RectangleD() {
        origin = new Point(0, 0);
    }

    // 构造函数：接收一个Point对象作为原点
    public RectangleD(Point p) {
        origin = p;
    }

    // 构造函数：接收坐标x和y，创建矩形
    public RectangleD(int x, int y) {
        origin = new Point(0, 0);
        width = x;
        height = y;
    }

    // 构造函数：接收原点坐标和宽高
    public RectangleD(Point p, int x, int y) {
        origin = p;
        width = x;
        height = y;
    }

    // 计算矩形面积
    public int getArea() {
        return width * height;
    }

    // 绘制矩形
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);

        g.drawRect(origin.x, origin.y, width, height);
    }
}
