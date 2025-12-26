package com.myblog.firstjavaproject.javawork.exp4.opera;


import com.myblog.firstjavaproject.javawork.exp4.book.BookList;

public interface IOPeration {//创建接口
    boolean work(BookList bookList);//抽象方法
    //功能主要是针对图书的，也就是针对书架。
}