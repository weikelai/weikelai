package com.myblog.firstjavaproject.test1;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-12
 * @Description: testPhone
 * @Version: 1.0
 */

public class PhoneTest {
    public static void main(String[] args) {
        //创建对象的类名
        Phone p = new Phone();
        p.brand = "小米";
        p.price = 1999.8;
        System.out.println(p.brand);
        System.out.println(p.price);

        p.call();
        p.playGame();
    }
}
