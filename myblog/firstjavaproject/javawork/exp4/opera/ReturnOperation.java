package com.myblog.firstjavaproject.javawork.exp4.opera;

import com.myblog.firstjavaproject.javawork.exp4.book.Book;
import com.myblog.firstjavaproject.javawork.exp4.book.BookList;

import java.util.Scanner;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description:
 * @Version: 1.0
 */

public class ReturnOperation implements IOPeration{
    @Override
    public boolean work(BookList bookList) {
        System.out.println("归还图书！");
        System.out.println("请输入要归还的图书名字:");
        Scanner scanner=new Scanner(System.in);
        String name=scanner.nextLine();
        int curentSize= bookList.getUsedSize();
        for(int i=0;i<curentSize;i++){
            Book temp=bookList.getBook(i);
            if((temp.getName().equals(name))&&temp.isBorrowed()){
                {
                    temp.setBorrowed(false);
                    System.out.println("归还成功！");
                    return false;
                }
            }
        }
        return false;
    }
}
