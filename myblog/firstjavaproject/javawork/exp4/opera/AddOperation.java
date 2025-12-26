package com.myblog.firstjavaproject.javawork.exp4.opera;

import com.myblog.firstjavaproject.javawork.exp4.book.BookList;
import com.myblog.firstjavaproject.javawork.exp4.book.Book;
import java.util.Scanner;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 增加
 * @Version: 1.0
 */

public class AddOperation implements IOPeration {
    @Override
    public boolean work(BookList bookList) {
        System.out.println("新增图书！");

        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入新增图书名字：");
        String name = scanner.nextLine();

        System.out.println("请输入新增图书作者：");
        String author = scanner.nextLine();

        System.out.println("请输入价格：");
        int price = scanner.nextInt();

        scanner.nextLine(); // 清掉输入缓冲区的换行

        System.out.println("请输入图书类型：");
        String type = scanner.nextLine();

        Book book = new Book(name, author, price, type);

        int currentSize = bookList.getUsedSize();

        // 判断是否重复
        for (int i = 0; i < currentSize; i++) {
            Book temp = bookList.getBook(i);
            if (temp.getName().equals(name)) {
                System.out.println("已经有这本书了！");
                return true;
            }
        }

        // 插入新书
        bookList.setBooks(book);
        bookList.setUsedSize(currentSize + 1);

        System.out.println("新增图书成功！");
        return true;
    }
}
