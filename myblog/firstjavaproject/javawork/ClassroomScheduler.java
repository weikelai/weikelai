    package com.myblog.firstjavaproject.javawork;
    import java.util.*;
    /**
     * @Author: 赖国燕
     * @CreateTime: 2025-11-29
     * @Description: 利用时隙分配算法来生成4-609教室的课表
     * @Version: 1.0
     */

    // 时隙类（课程）
    class TimeSlot {
        String courseName;  // 课程名称
        int startTime;      // 开始时间（用整数表示，如9表示9:00）
        int endTime;        // 结束时间

        public TimeSlot(String courseName, int startTime, int endTime) {
            this.courseName = courseName;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public String toString() {
            return String.format("%s [%d:00-%d:00]", courseName, startTime, endTime);
        }
    }

    // 时隙分配算法类
    class IntervalScheduling {

        /**
         * IS算法：时隙分配贪心算法
         * @param slots 时隙集合
         * @return 选中的时隙列表
         */
        public static List<TimeSlot> schedule(List<TimeSlot> slots) {
            int n = slots.size();
            if (n == 0) return new ArrayList<>();

            // ls1: 初始化 - 按结束时间从小到大排序
            Collections.sort(slots, (a, b) -> {
                if (a.endTime != b.endTime) {
                    return a.endTime - b.endTime;
                }
                return a.startTime - b.startTime;
            });

            // 设置是否分配某时隙的标志数组，初始化为1
            int[] flags = new int[n];
            Arrays.fill(flags, 1);

            // 存储选中的时隙
            List<TimeSlot> selectedSlots = new ArrayList<>();

            // ls2: 遍历所有时隙
            for (int i = 0; i < n; i++) {
                // ls3: 挑选时隙 - 如果当前时隙标志为1
                if (flags[i] == 1) {
                    // 将当前时隙加入结果集
                    selectedSlots.add(slots.get(i));

                    // 设置与之重叠的时隙标志为0
                    int currentEndTime = slots.get(i).endTime;

                    for (int j = i + 1; j < n; j++) {
                        // 如果后续时隙的开始时间小于当前时隙的结束时间，说明重叠
                        if (slots.get(j).startTime < currentEndTime) {
                            flags[j] = 0;  // 标记为不可用
                        }
                    }
                }
                // ls4: 标志为0直接返回，继续下一个时隙
            }

            return selectedSlots;
        }
    }

    // 主类
    public class ClassroomScheduler {

        public static void main(String[] args) {
            System.out.println("========================================");
            System.out.println("      4-609教室课表时隙分配系统");
            System.out.println("========================================\n");

            // 创建课程时隙集合（模拟多个课程申请使用教室）
            List<TimeSlot> requestedSlots = new ArrayList<>();

            // 添加课程申请（课程名，开始时间，结束时间）
            requestedSlots.add(new TimeSlot("高等数学", 8, 10));
            requestedSlots.add(new TimeSlot("大学英语", 9, 11));
            requestedSlots.add(new TimeSlot("数据结构", 10, 12));
            requestedSlots.add(new TimeSlot("计算机网络", 13, 15));
            requestedSlots.add(new TimeSlot("操作系统", 14, 16));
            requestedSlots.add(new TimeSlot("数据库原理", 15, 17));
            requestedSlots.add(new TimeSlot("密码学", 16, 18));
            requestedSlots.add(new TimeSlot("Java程序设计", 8, 9));
            requestedSlots.add(new TimeSlot("离散数学", 11, 13));
            requestedSlots.add(new TimeSlot("编译原理", 17, 19));

            // 显示所有申请的课程
            System.out.println("【所有课程申请】（共" + requestedSlots.size() + "门课程）");
            System.out.println("----------------------------------------");
            for (int i = 0; i < requestedSlots.size(); i++) {
                System.out.printf("%2d. %s\n", i + 1, requestedSlots.get(i));
            }

            // 使用IS算法进行时隙分配
            System.out.println("\n【执行时隙分配算法】");
            System.out.println("策略：每次选择结束时间最早的课程（贪心算法）");
            System.out.println("----------------------------------------");

            List<TimeSlot> selectedSlots = IntervalScheduling.schedule(requestedSlots);

            // 输出最终课表
            System.out.println("\n【4-609教室最终课表】（共安排" + selectedSlots.size() + "门课程）");
            System.out.println("========================================");

            for (int i = 0; i < selectedSlots.size(); i++) {
                TimeSlot slot = selectedSlots.get(i);
                System.out.printf("时段%d: %s\n", i + 1, slot);
            }

            System.out.println("========================================");

            // 统计信息
            System.out.println("\n【统计信息】");
            System.out.println("申请课程总数: " + requestedSlots.size());
            System.out.println("安排课程数量: " + selectedSlots.size());
            System.out.println("冲突课程数量: " + (requestedSlots.size() - selectedSlots.size()));
            System.out.printf("教室利用率: %.2f%%\n", (selectedSlots.size() * 100.0 / requestedSlots.size()));

            // 显示未安排的课程
            System.out.println("\n【未安排的课程】（需另行协调）");
            System.out.println("----------------------------------------");
            Set<String> selectedNames = new HashSet<>();
            for (TimeSlot slot : selectedSlots) {
                selectedNames.add(slot.courseName);
            }

            int count = 1;
            for (TimeSlot slot : requestedSlots) {
                if (!selectedNames.contains(slot.courseName)) {
                    System.out.printf("%2d. %s - 与已安排课程冲突\n", count++, slot);
                }
            }

            System.out.println("\n========================================");
            System.out.println("          排课完成！");
            System.out.println("========================================");
        }
    }
