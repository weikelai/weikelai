package simulation;

import algorithm.EdmondsKarp;
import algorithm.MinCut;
import graph.Graph;

/**
 * 攻击模拟器
 * 模拟DDoS攻击场景并分析防御策略
 */
public class AttackSimulator {
    private Graph graph;
    private TrafficGenerator trafficGenerator;
    private EdmondsKarp maxFlowAlgorithm;
    private MinCut minCutAnalyzer;

    public AttackSimulator(Graph graph) {
        this.graph = graph;
        this.trafficGenerator = new TrafficGenerator(graph);
        this.maxFlowAlgorithm = new EdmondsKarp();
        this.minCutAnalyzer = new MinCut(graph);
    }

    /**
     * 模拟攻击场景
     * @param normalTrafficRatio 正常流量比例
     * @param attackIntensity 攻击强度
     */
    public AttackResult simulateAttack(double normalTrafficRatio, int attackIntensity) {
        // 清空之前的流量
        trafficGenerator.clearTraffic();

        // 生成正常流量
        trafficGenerator.generateNormalTraffic(normalTrafficRatio);

        // 生成攻击流量
        trafficGenerator.generateAttackTraffic(attackIntensity);

        // 计算最大流
        int maxFlow = maxFlowAlgorithm.computeMaxFlow(graph);

        // 计算最小割
        minCutAnalyzer.computeMinCut();

        return new AttackResult(maxFlow, minCutAnalyzer);
    }

    /**
     * 对比不同攻击强度下的防御效果
     */
    public void compareAttackScenarios(int[] attackIntensities) {
        System.out.println("=== 不同攻击强度下的防御效果对比 ===");
        System.out.printf("%-15s %-15s %-15s %-15s%n", 
            "攻击强度", "最大流", "割边数量", "阻断代价");
        System.out.println("------------------------------------------------------------");

        for (int intensity : attackIntensities) {
            AttackResult result = simulateAttack(0.3, intensity);
            System.out.printf("%-15d %-15d %-15d %-15d%n",
                intensity,
                result.getMaxFlow(),
                result.getMinCut().getCutEdges().size(),
                result.getMinCut().getCutCost()
            );
        }
        System.out.println();
    }

    /**
     * 攻击结果类
     */
    public static class AttackResult {
        private int maxFlow;
        private MinCut minCut;

        public AttackResult(int maxFlow, MinCut minCut) {
            this.maxFlow = maxFlow;
            this.minCut = minCut;
        }

        public int getMaxFlow() {
            return maxFlow;
        }

        public MinCut getMinCut() {
            return minCut;
        }
    }
}

