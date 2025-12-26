package com.myblog.firstjavaproject.javawork.exp4.opera;
import com.myblog.firstjavaproject.javawork.exp4.book.BookList;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 退出系统
 * @Version: 1.0
 */


public class ExitOperation implements IOPeration{
    @Override
    public boolean work(BookList bookList) {
        System.out.println("正在退出当前用户...");
        return false;
    }
}
