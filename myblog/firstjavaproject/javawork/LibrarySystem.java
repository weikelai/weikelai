package com.myblog.firstjavaproject.javawork;
import java.io.*;
import java.util.*;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-05
 * @Description: 用对象流实现一简单的图书管理系统
 * @Version: 1.0
 */

/*
 ===================== 工具：JSON 简易存储（不依赖第三方库） =====================
*/
class JsonUtil {
    public static void saveText(String file, String text) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(text);
        } catch (IOException e) {
            System.out.println("写入失败：" + e.getMessage());
        }
    }

    public static String loadText(String file) {
        File f = new File(file);
        if (!f.exists()) return "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}

/*
 ===================== 图书类 =====================
*/
class Book {
    public String name;
    public String author;
    public int price;
    public boolean isBorrowed;

    public Book(String name, String author, int price) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.isBorrowed = false;
    }

    // 转 JSON
    public String toJson() {
        return String.format(
                "{\"name\":\"%s\",\"author\":\"%s\",\"price\":%d,\"borrowed\":%b}",
                name, author, price, isBorrowed
        );
    }

    // JSON 转 book
    public static Book fromJson(Map<String, Object> map) {
        Book b = new Book(
                (String) map.get("name"),
                (String) map.get("author"),
                ((Double) map.get("price")).intValue()
        );
        b.isBorrowed = (Boolean) map.get("borrowed");
        return b;
    }

    @Override
    public String toString() {
        return "《" + name + "》 作者：" + author + " 价格：" + price
                + (isBorrowed ? "（已借出）" : "（未借出）");
    }
}

/*
 ===================== 用户类（含账户、密码） =====================
*/
abstract class User {
    public String username;
    public String password;
    public boolean isAdmin;

    public User(String u, String p, boolean admin) {
        username = u;
        password = p;
        isAdmin = admin;
    }

    public abstract int menu();
    public abstract void doWork(int choice, LibraryDB db);
}

/*
 ===================== 管理员 =====================
*/
class AdminUser extends User {
    public AdminUser(String u, String p) { super(u, p, true); }

    @Override
    public int menu() {
        System.out.println("\n==== 管理员菜单 ====");
        System.out.println("1. 新增图书");
        System.out.println("2. 删除图书");
        System.out.println("3. 查找图书");
        System.out.println("4. 显示全部图书");
        System.out.println("0. 退出登录");
        System.out.print("选择： ");
        return new Scanner(System.in).nextInt();
    }

    @Override
    public void doWork(int choice, LibraryDB db) {
        Scanner sc = new Scanner(System.in);

        switch (choice) {
            case 1:
                System.out.print("书名：");  String name = sc.nextLine();
                System.out.print("作者：");   String a = sc.nextLine();
                System.out.print("价格：");   int price = sc.nextInt();
                db.addBook(new Book(name, a, price));
                System.out.println("添加成功！");
                break;

            case 2:
                System.out.print("要删除的书名：");
                if (db.removeBook(sc.nextLine())) System.out.println("删除成功！");
                else System.out.println("未找到该书！");
                break;

            case 3:
                System.out.print("查找书名：");
                Book b = db.findBook(sc.nextLine());
                System.out.println(b == null ? "未找到！" : b);
                break;

            case 4:
                db.showBooks();
                break;
        }
    }
}

/*
 ===================== 普通用户（可借书、还书） =====================
*/
class NormalUser extends User {
    public NormalUser(String u, String p) { super(u, p, false); }

    @Override
    public int menu() {
        System.out.println("\n==== 普通用户菜单 ====");
        System.out.println("1. 查找图书");
        System.out.println("2. 借阅图书");
        System.out.println("3. 归还图书");
        System.out.println("4. 显示全部图书");
        System.out.println("0. 退出登录");
        System.out.print("选择： ");
        return new Scanner(System.in).nextInt();
    }

    @Override
    public void doWork(int choice, LibraryDB db) {
        Scanner sc = new Scanner(System.in);

        switch (choice) {
            case 1:
                System.out.print("查找书名：");
                Book b = db.findBook(sc.nextLine());
                System.out.println(b == null ? "未找到！" : b);
                break;

            case 2:
                System.out.print("要借的书名：");
                if (db.borrowBook(sc.nextLine()))
                    System.out.println("借阅成功！");
                else
                    System.out.println("借阅失败（不存在或已被借出）！");
                break;

            case 3:
                System.out.print("要归还的书名：");
                if (db.returnBook(sc.nextLine()))
                    System.out.println("归还成功！");
                else
                    System.out.println("归还失败（不存在或未借出）！");
                break;

            case 4:
                db.showBooks();
                break;
        }
    }
}

/*
 ===================== 数据库（图书 + 用户） JSON 文件持久化 =====================
*/
class LibraryDB {
    private List<Book> books = new ArrayList<>();
    private Map<String, User> users = new HashMap<>();

    private static final String FILE = "library.json";

    public LibraryDB() {
        load();
    }

    // —— 图书相关 ——
    public void addBook(Book b) { books.add(b); save(); }

    public boolean removeBook(String name) {
        for (Book b : books) {
            if (b.name.equals(name)) {
                books.remove(b); save(); return true;
            }
        }
        return false;
    }

    public Book findBook(String name) {
        for (Book b : books) {
            if (b.name.equals(name)) return b;
        }
        return null;
    }

    public boolean borrowBook(String name) {
        Book b = findBook(name);
        if (b != null && !b.isBorrowed) {
            b.isBorrowed = true;
            save();
            return true;
        }
        return false;
    }

    public boolean returnBook(String name) {
        Book b = findBook(name);
        if (b != null && b.isBorrowed) {
            b.isBorrowed = false;
            save();
            return true;
        }
        return false;
    }

    public void showBooks() {
        if (books.isEmpty()) {
            System.out.println("没有图书！");
            return;
        }
        for (Book b : books) System.out.println(b);
    }

    // —— 用户相关 ——
    public boolean register(String u, String p, boolean admin) {
        if (users.containsKey(u)) return false;
        users.put(u, admin ? new AdminUser(u, p) : new NormalUser(u, p));
        save();
        return true;
    }

    public User login(String u, String p) {
        if (!users.containsKey(u)) return null;
        User usr = users.get(u);
        return usr.password.equals(p) ? usr : null;
    }

    // —— 保存为 JSON ——
    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"books\":[");

        // 保存 books
        for (int i = 0; i < books.size(); i++) {
            sb.append(books.get(i).toJson());
            if (i != books.size() - 1) sb.append(",");
        }
        sb.append("],\"users\":[");

        // 保存 users
        int idx = 0;
        for (User u : users.values()) {
            sb.append("{\"username\":\"").append(u.username)
                    .append("\",\"password\":\"").append(u.password)
                    .append("\",\"admin\":").append(u.isAdmin).append("}");
            if (idx != users.size() - 1) sb.append(",");
            idx++;
        }
        sb.append("]}");

        JsonUtil.saveText(FILE, sb.toString());
    }

    // —— 读取 JSON（使用简单解析） ——
    public void load() {
        String json = JsonUtil.loadText(FILE);
        if (json.isEmpty()) return;

        try {
            // 非严格 JSON 解析（适合本题）
            json = json.replace("{", "")
                    .replace("}", "")
                    .replace("[", "")
                    .replace("]", "");

            String[] sections = json.split("\"users\":");

            // 解析 books
            String booksPart = sections[0].replace("books:", "");
            if (!booksPart.trim().isEmpty()) {
                String[] bs = booksPart.split("borrowed");
                for (String s : bs) {
                    if (!s.contains("name")) continue;

                    String[] fields = s.split(",");
                    String name = fields[0].split(":")[1].replace("\"", "").trim();
                    String author = fields[1].split(":")[1].replace("\"", "").trim();
                    int price = Integer.parseInt(fields[2].split(":")[1].trim());
                    boolean borrowed = Boolean.parseBoolean(fields[3].replace(":", "").trim());

                    Book b = new Book(name, author, price);
                    b.isBorrowed = borrowed;
                    books.add(b);
                }
            }

            // 解析 users
            if (sections.length > 1) {
                String usersPart = sections[1];
                String[] us = usersPart.split("admin");
                for (String s : us) {
                    if (!s.contains("username")) continue;

                    String[] f = s.split(",");
                    String username = f[0].split(":")[1].replace("\"", "").trim();
                    String password = f[1].split(":")[1].replace("\"", "").trim();
                    boolean admin = Boolean.parseBoolean(f[2].replace(":", "").trim());

                    users.put(username, admin ?
                            new AdminUser(username, password) :
                            new NormalUser(username, password));
                }
            }

        } catch (Exception ignored) {}
    }
}

/*
 ===================== 主程序 =====================
*/
public class LibrarySystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LibraryDB db = new LibraryDB();

        System.out.println("===== 图书管理系统（JSON + 用户 + 借书）=====");

        while (true) {
            System.out.println("\n1. 登录");
            System.out.println("2. 注册普通用户");
            System.out.println("3. 注册管理员");
            System.out.println("0. 退出程序");
            System.out.print("选择： ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 0) {
                System.out.println("系统已退出！");
                break;
            }

            if (choice == 2 || choice == 3) {
                System.out.print("用户名：");
                String u = sc.nextLine();
                System.out.print("密码：");
                String p = sc.nextLine();

                if (db.register(u, p, choice == 3))
                    System.out.println("注册成功！");
                else
                    System.out.println("用户已存在！");
                continue;
            }

            if (choice == 1) {
                System.out.print("用户名：");
                String u = sc.nextLine();
                System.out.print("密码：");
                String p = sc.nextLine();

                User user = db.login(u, p);
                if (user == null) {
                    System.out.println("登录失败！");
                    continue;
                }

                System.out.println("欢迎你：" + user.username);

                // 用户循环
                while (true) {
                    int op = user.menu();
                    if (op == 0) break;
                    user.doWork(op, db);
                }
            }
        }
    }
}
