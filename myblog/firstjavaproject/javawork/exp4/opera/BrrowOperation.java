package com.myblog.firstjavaproject.javawork.exp4.opera;

import com.myblog.firstjavaproject.javawork.exp4.book.BookList;
import com.myblog.firstjavaproject.javawork.exp4.book.Book;
import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description:
 * @Version: 1.0
 */

public class BrrowOperation implements IOPeration{
    @Override
    public boolean work(BookList bookList) {
        System.out.println("借阅图书！");
        System.out.println("请输入要借阅的图书名字:");
        Scanner scanner=new Scanner(System.in);
        String name=scanner.nextLine();
        int curentSize= bookList.getUsedSize();
        int x=1;
        for(int i=0;i<curentSize;i++){
            Book temp=bookList.getBook(i);
            if((temp.getName().equals(name))&&!temp.isBorrowed()){
                {

                    temp.setBorrowed(true);
                    x=0;
                    System.out.println("借阅成功！");
                    return false;
                }
            }
        }
        if(x==1){
            System.out.println("没有该图书");
        }
        return false;
    }
}
