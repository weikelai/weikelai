package com.myblog.firstjavaproject.StudentSystemManage;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-09-18
 * @Description:
 * @Version: 1.0
 */

public class StudentSystem {
    public static void main(String[] args) {
        ArrayList<Student> list = new ArrayList<>();
        Loop:while(true){
            System.out.println("------------欢迎来到学生管理系统：------------");
            System.out.println("1.添加学生");
            System.out.println("2.删除学生");
            System.out.println("3.修改学生");
            System.out.println("4.查询学生");
            System.out.println("5.退出");
            Scanner sc = new Scanner(System.in);
            String choose = sc.next();
            switch (choose){
                case "1":
                    addStudent(list);
                    break;
                case "2":
                    deleteStudent(list);
                    break;
                case "3":
                    updateStudent(list);
                    break;
                case "4":
                    queryStudent(list);
                    break;
                case "5":
                    System.out.println("退出");
                    break Loop;
                default:
                    System.out.println("无选项");
                    break;
            }
        }
    }
    //添加
    public static void addStudent(ArrayList<Student> list){
        Student stu = new Student();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Input ID:");
            String id = sc.next();
            boolean flag = constainID(list,id);
            if(flag)
            {
                System.out.println("ID已经存在，请重新输入：");
            }else{
                stu.setId(id);
                break;
            }
        }
        System.out.println("Input name:");
        String name = sc.next();
        stu.setName(name);
        System.out.println("Input age:");
        int age = sc.nextInt();
        stu.setAge(age);
        System.out.println("Input address:");
        String address = sc.next();
        stu.setAddress(address);

        list.add(stu);
        System.out.println("学生信息添加成功！");
    }
    //删除
    public static void deleteStudent(ArrayList<Student> list){
        System.out.println("请输入要删除的ID:");
        Scanner sc = new Scanner(System.in);
        String id = sc.next();
        int index = getID(list,id);
        if(index >= 0){
            list.remove(index);
            System.out.println("成功删除id为"+id+"的学生");
        }else{
            System.out.println("id不存在,删除失败！");
            return;
        }
    }
    //更新
    public static void updateStudent(ArrayList<Student> list){
        System.out.println("请输入需要修改学生的ID:");
        Scanner sc = new Scanner(System.in);
        String id = sc.next();
        int index = getID(list,id);
        if(index == -1){
            System.out.println("id不存在，请重新输入：");
        }
        Student stu = list.get(index);
        System.out.println("Input update name:");
        String name = sc.next();
        stu.setName(name);
        System.out.println("Input update age:");
        int age = sc.nextInt();
        stu.setAge(age);
        System.out.println("Input update address:");
        String address = sc.next();
        stu.setAddress(address);

        System.out.println("学生信息修改成功！");
    }
    //查询
    public static void queryStudent(ArrayList<Student> list){
        if(list.size()==0){
            System.out.println("当前无学生信息，请添加后再查询");
        }
        System.out.println("id\t\t姓名\t年龄\t家庭住址");
        for(int i = 0;i < list.size();i++){
            Student stu = list.get(i);
            System.out.println(stu.getId()+"\t\t"+stu.getName()+"\t"+stu.getAge()+"\t"+ stu.getAddress());
        }
    }
    //判断ID是否存在
    public static boolean constainID(ArrayList<Student> list,String id){
        for(int i = 0;i < list.size();i++){
            Student stu = list.get(i);
            if(stu.equals(id)){
                return true;
            }
        }
        return false;
    }
    //通过ID获取索引
    public static int getID(ArrayList<Student> list,String id){
        for(int i = 0;i < list.size();i++){
            Student stu = list.get(i);
            String sid = stu.getId();
            if(sid.equals(id)){
                return i;
            }
        }
        return -1;
    }
}
