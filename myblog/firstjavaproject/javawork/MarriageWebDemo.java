package com.myblog.firstjavaproject.javawork;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用二分图算法构建婚介网站
 * Swing + 匈牙利算法
 */
public class MarriageWebDemo extends JFrame {

    private final String[] males = {"张三", "李四", "王五", "赵六", "孙七"};
    private final String[] females = {"小红", "小丽", "小芳", "小美", "小静"};

    private final int nMale = males.length;
    private final int nFemale = females.length;

    private List<Integer>[] graph;
    private int[] matchF;
    private boolean[] visited;

    private JTextArea resultArea;

    public MarriageWebDemo() {
        setTitle("Java 婚介网站（二分图匹配）");
        setSize(700, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initGraph();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("智能婚介匹配系统", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // 中间：男女列表
        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.add(new JScrollPane(new JList<>(males)));
        center.add(new JScrollPane(new JList<>(females)));
        add(center, BorderLayout.CENTER);

        // 底部
        JPanel bottom = new JPanel(new BorderLayout(5, 5));

        JButton btn = new JButton("开始匹配");
        btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        bottom.add(btn, BorderLayout.NORTH);

        // ===== 选项卡 =====
        JTabbedPane tabbedPane = new JTabbedPane();

        // 匹配规则说明
        JTextArea ruleArea = new JTextArea();
        ruleArea.setEditable(false);
        ruleArea.setText(
                "【匹配规则说明】\n\n" +
                        "1. 系统采用二分图最大匹配算法（匈牙利算法）。\n" +
                        "2. 男生和女生分别作为二分图的两个顶点集合。\n" +
                        "3. 每条边表示一名男生对某名女生有匹配意向。\n" +
                        "4. 通过寻找增广路径，得到最大数量的匹配对。\n" +
                        "5. 每名男生和每名女生最多只匹配一个对象。\n\n" +
                        "点击“开始匹配”按钮后，系统将自动计算最优匹配结果。"
        );

        // 匹配结果显示
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        tabbedPane.addTab("如何匹配", new JScrollPane(ruleArea));
        tabbedPane.addTab("匹配信息", new JScrollPane(resultArea));

        bottom.add(tabbedPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btn.addActionListener(e -> doMatch());
    }

    private void initGraph() {
        graph = new ArrayList[nMale];
        for (int i = 0; i < nMale; i++) {
            graph[i] = new ArrayList<>();
        }

        graph[0].add(0); graph[0].add(1);
        graph[1].add(1); graph[1].add(2);
        graph[2].add(2); graph[2].add(3);
        graph[3].add(0); graph[3].add(3); graph[3].add(4);
        graph[4].add(4);
    }

    private void doMatch() {
        matchF = new int[nFemale];
        Arrays.fill(matchF, -1);

        int cnt = 0;
        for (int i = 0; i < nMale; i++) {
            visited = new boolean[nFemale];
            if (dfs(i)) cnt++;
        }

        resultArea.setText("");
        resultArea.append("【匹配信息】\n\n");
        resultArea.append("男生人数：" + nMale + "\n");
        resultArea.append("女生人数：" + nFemale + "\n");
        resultArea.append("成功匹配对数：" + cnt + "\n\n");

        for (int j = 0; j < nFemale; j++) {
            if (matchF[j] != -1) {
                resultArea.append(
                        males[matchF[j]] + " ❤ " + females[j] + "\n"
                );
            }
        }
    }

    private boolean dfs(int male) {
        for (int female : graph[male]) {
            if (visited[female]) continue;
            visited[female] = true;

            if (matchF[female] == -1 || dfs(matchF[female])) {
                matchF[female] = male;
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new MarriageWebDemo().setVisible(true)
        );
    }
}
