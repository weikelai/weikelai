package util;

import entity.Node;
import graph.Graph;

import java.util.Scanner;

/**
 * 输入处理工具类
 * 用于从标准输入或文件读取网络拓扑数据
 */
public class InputParser {
    private Scanner scanner;

    public InputParser() {
        this.scanner = new Scanner(System.in);
    }

    public InputParser(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * 从标准输入构建图
     */
    public Graph parseGraph() {
        Graph graph = new Graph();

        System.out.println("请输入网络拓扑信息：");
        System.out.print("节点数量: ");
        int nodeCount = scanner.nextInt();

        // 读取节点信息
        for (int i = 0; i < nodeCount; i++) {
            System.out.printf("节点 %d (格式: id 名称 类型[SOURCE/SINK/ROUTER/HOST]): ", i + 1);
            int id = scanner.nextInt();
            String name = scanner.next();
            String typeStr = scanner.next().toUpperCase();
            
            Node.NodeType type = Node.NodeType.valueOf(typeStr);
            Node node = new Node(id, name, type);
            graph.addNode(node);
        }

        // 读取边信息
        System.out.print("边数量: ");
        int edgeCount = scanner.nextInt();
        
        for (int i = 0; i < edgeCount; i++) {
            System.out.printf("边 %d (格式: fromId toId 容量 [代价]): ", i + 1);
            int fromId = scanner.nextInt();
            int toId = scanner.nextInt();
            int capacity = scanner.nextInt();
            
            Node from = graph.findNodeById(fromId);
            Node to = graph.findNodeById(toId);
            
            if (scanner.hasNextInt()) {
                int cost = scanner.nextInt();
                graph.addEdge(from, to, capacity, cost);
            } else {
                graph.addEdge(from, to, capacity);
            }
        }

        return graph;
    }

    /**
     * 构建默认示例网络（用于演示）
     */
    public static Graph buildDefaultGraph() {
        Graph graph = new Graph();

        // 创建节点
        Node source = new Node(0, "攻击源", Node.NodeType.SOURCE);
        Node router1 = new Node(1, "路由器1", Node.NodeType.ROUTER);
        Node router2 = new Node(2, "路由器2", Node.NodeType.ROUTER);
        Node router3 = new Node(3, "路由器3", Node.NodeType.ROUTER);
        Node server = new Node(4, "目标服务器", Node.NodeType.SINK);

        graph.addNode(source);
        graph.addNode(router1);
        graph.addNode(router2);
        graph.addNode(router3);
        graph.addNode(server);

        // 创建边（容量，代价）
        graph.addEdge(source, router1, 100, 1);
        graph.addEdge(source, router2, 80, 1);
        graph.addEdge(router1, router3, 60, 2);
        graph.addEdge(router2, router3, 50, 2);
        graph.addEdge(router3, server, 90, 3);

        return graph;
    }

    /**
     * 关闭扫描器
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}

