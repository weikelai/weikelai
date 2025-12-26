package com.myblog.firstjavaproject.javawork.exp4.opera;

import com.myblog.firstjavaproject.javawork.exp4.book.Book;
import com.myblog.firstjavaproject.javawork.exp4.book.BookList;

import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 查找
 * @Version: 1.0
 */

public class FindOperation implements IOPeration {
    @Override
    public boolean work(BookList bookList) {
        System.out.println("查找图书！");
        System.out.println("请输入要查找的图书名字：");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();

        int currentSize = bookList.getUsedSize();
        for (int i = 0; i < currentSize; i++) {
            Book book = bookList.getBook(i);
            if (book.getName().equals(name)) {
                System.out.println("查到了：");
                System.out.println(book);
                return true;
            }
        }

        System.out.println("没有这本书！");
        return true;
    }
}
