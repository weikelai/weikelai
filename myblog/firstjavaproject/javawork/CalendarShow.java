package com.myblog.firstjavaproject.javawork;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
/**
 * @Author: 赖国燕
 * @CreateTime: 2025-11-22
 * @Description: 在控制台输出显示某年某月的日历
 * Java控制台打印日历程序
 * 功能：用户输入年份和月份，打印对应月份的日历
 * 如果是当前年月，则在当天日期后标注*符号
 * @Version: 1.0
 */

public class CalendarShow {

    // 成员变量
    private Calendar cal;        // 用于计算指定年月的日历
    private Calendar today;      // 保存当前系统日期
    private int inputYear;       // 用户输入的年份
    private int inputMonth;      // 用户输入的月份

    /**
     * 构造方法：根据用户输入的年月初始化Calendar对象
     * @param year 年份
     * @param month 月份（1-12）
     */
    public CalendarShow(int year, int month) {
        this.inputYear = year;
        this.inputMonth = month;
        // 初始化为指定年月的1日
        cal = new GregorianCalendar(year, month - 1, 1);
        // 保存当前系统日期，用于判断是否标记今天
        today = new GregorianCalendar();
    }

    /**
     * 判断指定年月是否为当前年月
     * @return 如果是当前年月返回true，否则返回false
     */
    private boolean isCurrentMonth() {
        return inputYear == today.get(Calendar.YEAR)
                && inputMonth == (today.get(Calendar.MONTH) + 1);
    }

    /**
     * 重写toString方法，生成完整的日历字符串
     * @return 格式化后的日历字符串
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // 1. 添加头部（年月 + 星期标题行）
        sb.append(top()).append("\n");

        // 2. 获取今天是几号（仅当输入年月等于当前年月时有效）
        int todayDate = today.get(Calendar.DAY_OF_MONTH);

        // 3. 获取本月1日是星期几（1=周日，2=周一，...，7=周六）
        int weekOfOne = cal.get(Calendar.DAY_OF_WEEK);

        // 4. 在1日前添加空格占位，使1日显示在正确的星期列下方
        for (int i = Calendar.SUNDAY; i < weekOfOne; i++) {
            sb.append("  \t"); // 两个空格 + 制表符
        }

        // 5. 获取当前设置的月份（用于判断是否还在本月）
        int month = cal.get(Calendar.MONTH);

        // 6. 循环输出本月所有日期
        while (month == cal.get(Calendar.MONTH)) {
            int day = cal.get(Calendar.DAY_OF_MONTH);

            // 日期小于10时，添加前导空格保持对齐
            if (day < 10) {
                sb.append(" ").append(day);
            } else {
                sb.append(day);
            }

            // 判断是否为当天（仅当输入年月等于当前年月时标记）
            if (isCurrentMonth() && day == todayDate) {
                sb.append("* ");
            } else {
                sb.append("\t");
            }

            // 如果是星期六，换行
            int week = cal.get(Calendar.DAY_OF_WEEK);
            if (week == Calendar.SATURDAY) {
                sb.append("\n");
            }

            // 日期加1，继续下一天
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return sb.toString();
    }

    /**
     * 生成日历头部信息
     * @return 包含年月和星期标题的字符串
     */
    private String top() {
        String yearAndMonth = inputYear + "年" + inputMonth + "月";
        String weekHeader = "日\t一\t二\t三\t四\t五\t六";
        return yearAndMonth + "\n" + weekHeader;
    }

    /**
     * 主方法：程序入口
     * 接收用户输入的年份和月份，打印对应日历
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========== 日历打印程序 ==========");

        // 输入年份
        System.out.print("请输入年份：");
        int year = scanner.nextInt();

        // 输入月份（带输入验证）
        int month;
        while (true) {
            System.out.print("请输入月份（1-12）：");
            month = scanner.nextInt();
            if (month >= 1 && month <= 12) {
                break;
            }
            System.out.println("输入错误！月份必须在1-12之间，请重新输入。");
        }

        // 创建日历对象并打印
        System.out.println();
        CalendarShow cs = new CalendarShow(year, month);
        System.out.println(cs);

        scanner.close();
    }
}