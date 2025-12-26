package com.myblog.firstjavaproject.ArrayListDemo;

import java.util.ArrayList;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-12
 * @Description:
 * @Version: 1.0
 */

public class ArraylistDemo1 {
    public static void main(String[] args) {
        //创建集合
        ArrayList<String> list = new ArrayList<>();
        //添加元素
        boolean result = list.add("aaa");
        System.out.println(result);
        System.out.println(list);
    }
}
