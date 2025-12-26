package com.myblog.firstjavaproject.javawork.PiecewiseLeastSquaresDemo;
import java.util.ArrayList;
import java.util.List;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-20
 * @Description: 分段最小二乘拟合管理类
 * @Version: 1.0
 */

public class PiecewiseFitting {

    /**
     * 表示一个分段拟合结果
     */
    public static class Segment {
        public int start;
        public int end;
        public double a;
        public double b;

        public Segment(int start, int end, double a, double b) {
            this.start = start;
            this.end = end;
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            String sign = b >= 0 ? "+" : "-";
            return String.format(
                    "区间 [%d, %d] : y = %.4fx %s %.4f",
                    start, end, a, sign, Math.abs(b)
            );
        }

    }

    /**
     * 按给定分段点进行分段最小二乘拟合
     *
     * @param x          自变量数组
     * @param y          因变量数组
     * @param splitIndex 分段点数组（如 {3, 6}）
     * @return 拟合结果列表
     */
    public static List<Segment> fit(double[] x, double[] y, int[] splitIndex) {
        List<Segment> result = new ArrayList<>();

        int start = 0;
        for (int split : splitIndex) {
            double[] coef = LeastSquaresUtil.fit(x, y, start, split);
            result.add(new Segment(start, split, coef[0], coef[1]));
            start = split + 1;
        }

        // 最后一段
        if (start < x.length - 1) {
            double[] coef = LeastSquaresUtil.fit(x, y, start, x.length - 1);
            result.add(new Segment(start, x.length - 1, coef[0], coef[1]));
        }

        return result;
    }
}

