package com.myblog.firstjavaproject.Student_Static_class;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-23
 * @Description: 基本构造Student
 * @Version: 1.0
 */

public class Student {
    //姓名，性别，年龄
        private String name;
        private String gender;
        private int age;
        static public String teachername;

    public Student() {
    }

    public Student(String name, String gender, int age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public void Study(){
        System.out.println(name+"正在学习");
    }
    public void show(){
        System.out.println(name+" "+gender+" "+age+" "+teachername);
    }
}
