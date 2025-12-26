package com.myblog.firstjavaproject.ArrayListDemo;

import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-11-15
 * @Description: 修改后的逆序数算法（支持相同元素）
 * @Version: 2.0
 */
public class InversionCount {

    // 归并排序统计逆序对（主函数）
    public static long inventfenzhi(int[] arry, int left, int right) {
        if (left >= right) return 0;

        int mid = left + (right - left) / 2; // 防止溢出
        long count = 0;

        // 递归处理左半部分
        count += inventfenzhi(arry, left, mid);
        // 递归处理右半部分
        count += inventfenzhi(arry, mid + 1, right);
        // 合并并统计跨区间逆序对
        count += mergenixushu(arry, left, mid, right);

        return count;
    }

    // 合并函数（已修改支持相同元素）
    public static long mergenixushu(int[] arry, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        long count = 0;

        while (i <= mid && j <= right) {
            if (arry[i] <= arry[j]) {  // 修改点：加入等号
                temp[k++] = arry[i++];
            } else {
                temp[k++] = arry[j++];
                count += (mid - i + 1);
            }
        }

        while (i <= mid) temp[k++] = arry[i++];
        while (j <= right) temp[k++] = arry[j++];
        System.arraycopy(temp, 0, arry, left, temp.length);

        return count;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入数组元素，用空格或逗号分隔：");
        String input = scanner.nextLine().trim();
        String[] elements = input.split("[\\s,]+");

        if (elements.length == 0 || elements[0].isEmpty()) {
            System.out.println("输入为空，程序结束。");
            scanner.close();
            return;
        }

        int[] arry = new int[elements.length];
        try {
            for (int i = 0; i < elements.length; i++) {
                arry[i] = Integer.parseInt(elements[i]);
            }
        } catch (NumberFormatException e) {
            System.out.println("输入格式错误，请输入有效整数。");
            scanner.close();
            return;
        }

        long count = inventfenzhi(arry, 0, arry.length - 1);

        System.out.println("数组长度：" + arry.length);
        System.out.println("逆序对数：" + count);
        System.out.print("排序后的数组：");
        for (int num : arry) {
            System.out.print(num + " ");
        }
        System.out.println();

        scanner.close();
    }
}