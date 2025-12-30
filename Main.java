import algorithm.EdmondsKarp;
import graph.Graph;
import simulation.AttackSimulator;
import util.InputParser;
import util.OutputPrinter;

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

        // 对比不同攻击强度
        int[] attackIntensities = {20, 50, 80, 100, 150};
        simulator.compareAttackScenarios(attackIntensities);

        System.out.println("========================================");
        System.out.println("实验完成！");
        System.out.println("========================================");
    }
}
