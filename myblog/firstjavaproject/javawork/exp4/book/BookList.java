package com.myblog.firstjavaproject.javawork.exp4.book;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 书架, 我们希望能在里面固定的位置放书，并且能知道放了几本书
 * @Version: 1.0
 */

//书架
public class BookList {
    private static final int DEFAULT_SIZE=10;
    private  Book[] books=new  Book[DEFAULT_SIZE];//这个书架可以放十本书
    public BookList(){//构造方法
        books[0]=new Book("《三国演义》","罗贯中",40,"小说");
        books[1]=new Book("《西游记》","吴承恩",60,"小说");
        books[2]=new Book("《红楼梦》","曹雪芹",45,"小说");
        this.usedSize=3;
    }
    //通过这个方法，实现ShowOperation中的通过下标打印数组元素
    public Book getBook(int pos){
        return this.books[pos];
    }
    public void setBooks(Book book){
        this.books[usedSize]=book;
    }
    public void setBooks(int pos,Book book){
        this.books[pos]=book;
    }
    private int usedSize;//记录下当前book数组中有几本书
    public int getUsedSize() {
        return usedSize;
    }
    public void setUsedSize(int usedSize) {
        this.usedSize = usedSize;
    }
}
