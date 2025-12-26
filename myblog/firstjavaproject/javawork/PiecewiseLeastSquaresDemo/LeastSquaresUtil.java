package com.myblog.firstjavaproject.javawork.PiecewiseLeastSquaresDemo;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-20
 * @Description: 最小二乘核心算法
 * @Version: 1.0
 */

public class LeastSquaresUtil {

    /**
     * 对指定区间的数据进行最小二乘线性拟合
     * y = ax + b
     *
     * @param x     自变量数组
     * @param y     因变量数组
     * @param start 起始索引（包含）
     * @param end   结束索引（包含）
     * @return double[]{a, b}
     */
    public static double[] fit(double[] x, double[] y, int start, int end) {
        if (x == null || y == null || x.length != y.length) {
            throw new IllegalArgumentException("x 和 y 长度不一致");
        }
        if (start < 0 || end >= x.length || start >= end) {
            throw new IllegalArgumentException("分段区间不合法");
        }

        int n = end - start + 1;
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        for (int i = start; i <= end; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        double denominator = n * sumX2 - sumX * sumX;
        if (denominator == 0) {
            throw new ArithmeticException("无法进行线性拟合（分母为0）");
        }

        double a = (n * sumXY - sumX * sumY) / denominator;
        double b = (sumY - a * sumX) / n;

        return new double[]{a, b};
    }
}

