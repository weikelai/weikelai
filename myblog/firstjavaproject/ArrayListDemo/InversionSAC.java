package com.myblog.firstjavaproject.ArrayListDemo;

import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-11-15
 * @Description: SAC算法修改版（支持相同元素）
 * @Version: 2.0
 */
public class InversionSAC {

    public static int sortAndCount(int[] S) {
        if (S.length <= 1) return 0;

        int len = S.length / 2;
        int[] A = new int[len];
        int[] B = new int[S.length - len];

        // 分解序列
        for (int i = 0; i < len; i++) {
            A[i] = S[i];
        }
        for (int i = 0; i < S.length - len; i++) {
            B[i] = S[len + i];
        }

        // 递归求解
        int ra = sortAndCount(A);
        int rb = sortAndCount(B);
        int r = mergeAndCount(A, B, S);

        return r + ra + rb;
    }

    public static int mergeAndCount(int[] A, int[] B, int[] S) {
        int i = 0, j = 0, k = 0;
        int count = 0;

        while (i < A.length && j < B.length) {
            if (A[i] <= B[j]) {  // 修改点：加入等号
                S[k++] = A[i++];
            } else {
                count += A.length - i;
                S[k++] = B[j++];
            }
        }

        while (i < A.length) S[k++] = A[i++];
        while (j < B.length) S[k++] = B[j++];

        return count;
    }

    public static void main(String[] args) {
        System.out.println("请输入待计数逆序的整数序列（以空格分开）:");
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        String[] tokens = line.split("\\s+");

        int[] S1 = new int[tokens.length];
        try {
            for (int i = 0; i < tokens.length; i++) {
                S1[i] = Integer.parseInt(tokens[i]);
            }
        } catch (NumberFormatException e) {
            System.out.println("输入格式错误，请确保输入有效整数。");
            return;
        }

        int count = sortAndCount(S1);

        System.out.println("\n逆序对数: " + count);
        System.out.print("排序后的数组: ");
        for (int num : S1) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}