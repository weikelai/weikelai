package util;

import algorithm.MinCut;
import entity.Edge;
import graph.Graph;

import java.util.List;

/**
 * 输出工具类
 * 格式化输出计算结果和防御建议
 */
public class OutputPrinter {
    /**
     * 打印最大流结果
     */
    public static void printMaxFlowResult(int maxFlow, String algorithmName) {
        System.out.println("=== 最大流计算结果 ===");
        System.out.println("算法: " + algorithmName);
        System.out.println("最大流值: " + maxFlow);
        System.out.println("说明: 网络在攻击条件下可承载的最大流量为 " + maxFlow);
        System.out.println();
    }

    /**
     * 打印最小割结果
     */
    public static void printMinCutResult(MinCut minCut) {
        minCut.printMinCut();
    }

    /**
     * 打印防御建议
     */
    public static void printDefenseRecommendations(MinCut minCut, int maxFlow) {
        System.out.println("=== 防御建议 ===");
        
        List<Edge> cutEdges = minCut.getCutEdges();
        
        if (cutEdges.isEmpty()) {
            System.out.println("当前网络可以承受攻击流量，无需阻断连接。");
        } else {
            System.out.println("建议阻断以下连接以最小代价防御DDoS攻击：");
            System.out.println();
            
            int totalCost = 0;
            for (int i = 0; i < cutEdges.size(); i++) {
                Edge edge = cutEdges.get(i);
                totalCost += edge.getCost();
                System.out.printf("%d. 阻断连接: %s -> %s%n",
                    i + 1,
                    edge.getFrom().getName(),
                    edge.getTo().getName()
                );
                System.out.printf("   容量: %d, 代价: %d%n",
                    edge.getCapacity(),
                    edge.getCost()
                );
            }
            
            System.out.println();
            System.out.println("总阻断代价: " + totalCost);
            System.out.println("阻断后，攻击流量将被限制在 " + maxFlow + " 以内。");
        }
        
        System.out.println();
    }

    /**
     * 打印网络状态摘要
     */
    public static void printNetworkSummary(Graph graph, int maxFlow, MinCut minCut) {
        System.out.println("=== 网络状态摘要 ===");
        System.out.println("网络节点数: " + graph.getNodeCount());
        System.out.println("网络边数: " + graph.getEdgeCount());
        System.out.println("最大可承载流量: " + maxFlow);
        System.out.println("最小割边数: " + minCut.getCutEdges().size());
        System.out.println("最小阻断代价: " + minCut.getCutCost());
        System.out.println();
    }

    /**
     * 打印所有边的流量状态
     */
    public static void printFlowStatus(Graph graph) {
        System.out.println("=== 各边流量状态 ===");
        for (Edge edge : graph.getEdges()) {
            double utilization = edge.getCapacity() > 0 
                ? (double) edge.getFlow() / edge.getCapacity() * 100 
                : 0;
            
            System.out.printf("%s -> %s: 流量=%d/%d (利用率=%.2f%%)%n",
                edge.getFrom().getName(),
                edge.getTo().getName(),
                edge.getFlow(),
                edge.getCapacity(),
                utilization
            );
        }
        System.out.println();
    }
}

