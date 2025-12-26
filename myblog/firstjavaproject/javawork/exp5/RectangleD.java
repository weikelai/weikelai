package com.myblog.firstjavaproject.javawork.exp5;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-06
 * @Description: 矩形类
 * @Version: 1.0
 */

import javax.swing.*;
import java.awt.*;

public class RectangleD extends JPanel {
    private Point origin;
    private int width;
    private int height;

    public RectangleD(Point origin, int width, int height) throws RectangleException {

        if (origin.x < 0 || origin.y < 0)
            throw new RectangleException("坐标不能为负数！");

        if (width <= 0 || height <= 0)
            throw new RectangleException("宽度和高度必须为正数！");

        this.origin = origin;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLUE);
        g.drawRect(origin.x, origin.y, width, height);
    }
}