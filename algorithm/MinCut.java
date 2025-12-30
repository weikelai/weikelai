package algorithm;

import entity.Edge;
import entity.Node;
import graph.Graph;

import java.util.*;

/**
 * 最小割分析
 * 根据最大流-最小割定理，找到最小代价的阻断边集
 */
public class MinCut {
    private Graph graph;
    private Set<Node> sourceSide;  // 源点侧节点集合
    private Set<Node> sinkSide;   // 汇点侧节点集合
    private List<Edge> cutEdges;   // 割边集合

    public MinCut(Graph graph) {
        this.graph = graph;
        this.sourceSide = new HashSet<>();
        this.sinkSide = new HashSet<>();
        this.cutEdges = new ArrayList<>();
    }

    /**
     * 计算最小割
     * 在最大流计算完成后，通过BFS从源点遍历残余网络，找到可达节点集合
     */
    public void computeMinCut() {
        sourceSide.clear();
        sinkSide.clear();
        cutEdges.clear();

        Node source = graph.getSource();
        if (source == null) {
            return;
        }

        // 使用BFS从源点遍历，找到所有在残余网络中可达的节点
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.offer(source);
        visited.add(source);
        sourceSide.add(source);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // 遍历所有出边
            for (Edge edge : graph.getOutEdges(current)) {
                Node next = edge.getTo();
                
                // 如果边未饱和且节点未访问，则可达
                if (!edge.isSaturated() && !visited.contains(next)) {
                    visited.add(next);
                    sourceSide.add(next);
                    queue.offer(next);
                }
            }
        }

        // 汇点侧是源点侧的补集
        for (Node node : graph.getNodes()) {
            if (!sourceSide.contains(node)) {
                sinkSide.add(node);
            }
        }

        // 找到所有从源点侧指向汇点侧的饱和边（割边）
        for (Edge edge : graph.getEdges()) {
            if (sourceSide.contains(edge.getFrom()) && 
                sinkSide.contains(edge.getTo()) && 
                edge.isSaturated()) {
                cutEdges.add(edge);
            }
        }
    }

    /**
     * 计算最小割的代价
     */
    public int getCutCost() {
        int totalCost = 0;
        for (Edge edge : cutEdges) {
            totalCost += edge.getCost();
        }
        return totalCost;
    }

    /**
     * 计算最小割的容量（等于最大流值）
     */
    public int getCutCapacity() {
        int totalCapacity = 0;
        for (Edge edge : cutEdges) {
            totalCapacity += edge.getCapacity();
        }
        return totalCapacity;
    }

    /**
     * 获取源点侧节点集合
     */
    public Set<Node> getSourceSide() {
        return new HashSet<>(sourceSide);
    }

    /**
     * 获取汇点侧节点集合
     */
    public Set<Node> getSinkSide() {
        return new HashSet<>(sinkSide);
    }

    /**
     * 获取割边集合
     */
    public List<Edge> getCutEdges() {
        return new ArrayList<>(cutEdges);
    }

    /**
     * 打印最小割信息
     */
    public void printMinCut() {
        System.out.println("=== 最小割分析结果 ===");
        System.out.println("源点侧节点数量: " + sourceSide.size());
        System.out.println("汇点侧节点数量: " + sinkSide.size());
        System.out.println("割边数量: " + cutEdges.size());
        System.out.println("最小割容量: " + getCutCapacity());
        System.out.println("最小割代价: " + getCutCost());
        
        System.out.println("\n源点侧节点:");
        for (Node node : sourceSide) {
            System.out.println("  " + node.getName());
        }
        
        System.out.println("\n汇点侧节点:");
        for (Node node : sinkSide) {
            System.out.println("  " + node.getName());
        }
        
        System.out.println("\n需要阻断的连接（割边）:");
        for (Edge edge : cutEdges) {
            System.out.println(String.format(
                "  %s -> %s (容量: %d, 代价: %d)",
                edge.getFrom().getName(),
                edge.getTo().getName(),
                edge.getCapacity(),
                edge.getCost()
            ));
        }
        System.out.println();
    }
}

