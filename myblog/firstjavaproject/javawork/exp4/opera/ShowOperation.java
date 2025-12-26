package com.myblog.firstjavaproject.javawork.exp4.opera;
import com.myblog.firstjavaproject.javawork.exp4.book.BookList;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 显示图书
 * @Version: 1.0
 */

public class ShowOperation implements IOPeration{
    @Override
    public boolean work(BookList bookList) {
        System.out.println("显示图书！");
        int currentSize= bookList.getUsedSize();
        for (int i=0;i<currentSize;i++){
            System.out.println(bookList.getBook(i));
        }
        return false;
    }
}
