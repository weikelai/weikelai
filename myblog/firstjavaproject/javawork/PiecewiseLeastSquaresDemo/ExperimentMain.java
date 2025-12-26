package com.myblog.firstjavaproject.javawork.PiecewiseLeastSquaresDemo;
import java.util.List;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-20
 * @Description: 实验主程序
 * @Version: 1.0
 */

public class ExperimentMain {

    public static void main(String[] args) {

        // ===== 实验数据 6 =====
        double[] x = {0, 1, 2, 3, 4, 5, 6, 7};
        double[] y = {0, 2, 4, 6, 5, 4, 3, 2};

        // ===== 分段点设置 =====
        int[] splitIndex = {3};



        // ===== 执行分段最小二乘拟合 =====
        List<PiecewiseFitting.Segment> segments =
                PiecewiseFitting.fit(x, y, splitIndex);

        // ===== 输出结果 =====
        System.out.println("分段最小二乘法拟合结果：");
        for (PiecewiseFitting.Segment segment : segments) {
            System.out.println(segment);
        }
    }
}
