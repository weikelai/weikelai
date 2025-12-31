import algorithm.EdmondsKarp;
import graph.Graph;
import simulation.AttackSimulator;
import util.InputParser;
import util.OutputPrinter;
import entity.Node;

/**
 * 主程序入口
 * 基于网络流算法的 DDoS 攻击流量识别与最小代价阻断策略设计
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  DDoS 攻击流量识别与防御策略分析系统");
        System.out.println("  基于网络流算法（Edmonds-Karp）");
        System.out.println("========================================");
        System.out.println();

        // 构建网络拓扑（使用默认示例）
        Graph graph = InputParser.buildDefaultGraph();
        // 方案1：增加并行汇聚链路（路由器1/2 -> 目标服务器），设置不同容量/代价
        addParallelConvergingLinks(graph);
        
        // 打印网络拓扑
        graph.printGraph();

        // 创建攻击模拟器
        AttackSimulator simulator = new AttackSimulator(graph);

        // 模拟攻击场景
        System.out.println("开始模拟 DDoS 攻击场景...");
        System.out.println();
        
        // 设置攻击参数
        double normalTrafficRatio = 0.3;  // 正常流量占30%
        int attackIntensity = 50;          // 攻击强度

        // 执行攻击模拟
        AttackSimulator.AttackResult result = simulator.simulateAttack(
            normalTrafficRatio, 
            attackIntensity
        );

        // 输出最大流结果
        EdmondsKarp algorithm = new EdmondsKarp();
        OutputPrinter.printMaxFlowResult(
            result.getMaxFlow(), 
            algorithm.getAlgorithmName()
        );

        // 输出最小割结果
        OutputPrinter.printMinCutResult(result.getMinCut());

        // 输出防御建议
        OutputPrinter.printDefenseRecommendations(
            result.getMinCut(), 
            result.getMaxFlow()
        );

        // 输出网络状态摘要
        OutputPrinter.printNetworkSummary(
            graph, 
            result.getMaxFlow(), 
            result.getMinCut()
        );

        // 输出各边流量状态
        OutputPrinter.printFlowStatus(graph);

        // 对比不同攻击强度，使用 AttackSimulator 内部深拷贝与代价/容量动态
        int[] attackIntensities = {20, 50, 80, 100, 150};
        simulator.compareAttackScenarios(attackIntensities);

        System.out.println("========================================");
        System.out.println("实验完成！");
        System.out.println("========================================");
    }

    // 增加并行汇聚链路：路由器1->服务器、路由器2->服务器，容量与代价不同
    private static void addParallelConvergingLinks(Graph graph) {
        Node router1 = graph.findNodeByName("路由器1");
        Node router2 = graph.findNodeByName("路由器2");
        Node server = graph.findNodeByName("目标服务器");
        if (router1 != null && router2 != null && server != null) {
            graph.addEdge(router1, server, 40, 2); // 较小容量、较低代价
            graph.addEdge(router2, server, 30, 4); // 更小容量、较高代价
        }
    }

}
