package simulation;

import algorithm.EdmondsKarp;
import algorithm.MinCut;
import entity.Edge;
import graph.Graph;

import java.util.HashMap;
import java.util.Map;

/**
 * 攻击模拟器
 * 模拟DDoS攻击场景并分析防御策略
 */

public class AttackSimulator {
    private Graph graph;
    private TrafficGenerator trafficGenerator;
    private EdmondsKarp maxFlowAlgorithm;
    private MinCut minCutAnalyzer;
    private Map<Edge, Integer> originalCapacities;  // 保存原始容量
    private Map<Edge, Integer> originalCosts;        // 保存原始代价

    // 攻击强度影响参数
    private static final int MAX_INTENSITY = 150;     // 最大攻击强度（用于容量按比例缩减）
    private static final int MIN_CAPACITY = 1;        // 最小容量，防止为0
    private static final double COST_FACTOR = 0.05;   // 代价线性增长因子（针对指向SINK的边）
    private static final int DEGRADE_THRESHOLD = 90;   // 链路退化阈值

    public AttackSimulator(Graph graph) {
        this.graph = graph;
        this.trafficGenerator = new TrafficGenerator(graph);
        this.maxFlowAlgorithm = new EdmondsKarp();
        this.minCutAnalyzer = new MinCut(graph);
        // 保存原始容量
        this.originalCapacities = graph.saveOriginalCapacities();
        // 保存原始代价
        this.originalCosts = new HashMap<>();
        for (Edge e : graph.getEdges()) {
            this.originalCosts.put(e, e.getCost());
        }
    }

    /**
     * 根据攻击强度调整边容量
     * 攻击强度越大，链路拥塞越严重，有效容量越低
     * @param attackIntensity 攻击强度
     */
    private void adjustCapacitiesByAttackIntensity(int attackIntensity) {
        // 方案1（保留）：按强度线性缩减容量
        // effectiveCapacity = baseCapacity * (1 - intensity / MAX_INTENSITY)
        double ratio = Math.max(0.0, 1.0 - (double) attackIntensity / (double) MAX_INTENSITY);
        for (Edge edge : graph.getEdges()) {
            int baseCapacity = originalCapacities.get(edge);
            int effectiveCapacity = (int) Math.round(baseCapacity * ratio);
            effectiveCapacity = Math.max(effectiveCapacity, MIN_CAPACITY);
            edge.setCapacity(effectiveCapacity);
        }
        // 方案3：高强度触发链路退化（容量再减半）
        if (attackIntensity > DEGRADE_THRESHOLD) {
            for (Edge edge : graph.getEdges()) {
                int degraded = Math.max(MIN_CAPACITY, edge.getCapacity() / 2);
                edge.setCapacity(degraded);
            }
        }
    }

    /**
     * 根据攻击强度调整边代价（重点）
     * 指向汇点（SINK）的边代价随强度线性增加
     */
    private void adjustCostsByAttackIntensity(int attackIntensity) {
        for (Edge edge : graph.getEdges()) {
            int baseCost = originalCosts.get(edge);
            boolean toSink = edge.getTo() != null && edge.getTo().getType() == entity.Node.NodeType.SINK;
            if (toSink) {
                int adjusted = baseCost + (int) Math.round(attackIntensity * COST_FACTOR);
                edge.setCost(Math.max(adjusted, baseCost));
            } else {
                edge.setCost(baseCost);
            }
        }
    }

    /** 恢复所有边的原始代价 */
    private void restoreOriginalCosts() {
        for (Map.Entry<Edge, Integer> entry : originalCosts.entrySet()) {
            entry.getKey().setCost(entry.getValue());
        }
    }

    /**
     * 模拟攻击场景
     * @param normalTrafficRatio 正常流量比例
     * @param attackIntensity 攻击强度
     */
    public AttackResult simulateAttack(double normalTrafficRatio, int attackIntensity) {
        // 恢复原始容量（确保每次模拟从原始状态开始）
        graph.restoreOriginalCapacities(originalCapacities);
        // 恢复原始代价
        restoreOriginalCosts();
        
        // 根据攻击强度动态调整边容量
        // 攻击强度越大，链路拥塞越严重，可用容量越低
        adjustCapacitiesByAttackIntensity(attackIntensity);
        // 根据攻击强度动态调整代价（重点）
        adjustCostsByAttackIntensity(attackIntensity);

        // 清空之前的流量
        trafficGenerator.clearTraffic();

        // 生成正常流量
        trafficGenerator.generateNormalTraffic(normalTrafficRatio);

        // 生成攻击流量
        trafficGenerator.generateAttackTraffic(attackIntensity);

        // 计算最大流（此时容量已受攻击强度影响）
        int maxFlow = maxFlowAlgorithm.computeMaxFlow(graph);

        // 计算最小割
        minCutAnalyzer.computeMinCut();

        return new AttackResult(maxFlow, minCutAnalyzer);
    }

    /**
     * 对比不同攻击强度下的防御效果
     */
    public void compareAttackScenarios(int[] attackIntensities) {
        System.out.println("=== 不同攻击强度下的防御效果对比（深拷贝场景） ===");
        System.out.printf("%-15s %-15s %-15s %-15s%n", "攻击强度", "最大流", "割边数量", "阻断代价");
        System.out.println("------------------------------------------------------------");

        for (int intensity : attackIntensities) {
            // 方案4：每次使用 Graph 深拷贝，独立场景评估
            Graph scenarioGraph = this.graph.deepCopy();
            AttackSimulator scenarioSimulator = new AttackSimulator(scenarioGraph);
            AttackResult result = scenarioSimulator.simulateAttack(0.3, intensity);
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

