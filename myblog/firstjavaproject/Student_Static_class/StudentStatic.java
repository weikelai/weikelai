package com.myblog.firstjavaproject.Student_Static_class;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-23
 * @Description: 静态类学习
 * @Version: 1.0
 */

public class StudentStatic {
    public static void main(String[] args) {
        Student.teachername = "王老师";
        Student s1 = new Student();
        s1.setName("赖国燕");
        s1.setGender("女");
        s1.setAge(18);
        Student s2 = new Student();
        s2.setName("小米");
        s2.setGender("女");
        s2.setAge(19);
        s1.Study();
        s1.show();
        s2.show();
    }
}
