package com.myblog.firstjavaproject.StringBuilderDemo;

import java.util.StringJoiner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-12
 * @Description: Stringjoiner
 * @Version: 1.0
 */

public class Stringjoiner {
    public static void main(String[] args) {
        StringJoiner sj = new StringJoiner(",","[","]");
        sj.add("aaa").add("bbb").add("ccc");
        System.out.println(sj);
    }
}
