package simulation;

import entity.Edge;
import entity.Node;
import graph.Graph;

import java.util.Random;

/**
 * 流量生成器
 * 用于生成正常流量和攻击流量
 */
public class TrafficGenerator {
    private Random random;
    private Graph graph;

    public TrafficGenerator(Graph graph) {
        this.graph = graph;
        this.random = new Random();
    }

    /**
     * 生成正常流量
     * 在现有边容量基础上，随机分配正常流量
     */
    public void generateNormalTraffic(double trafficRatio) {
        // 正常流量通常占用总容量的较小比例
        for (Edge edge : graph.getEdges()) {
            int normalFlow = (int) (edge.getCapacity() * trafficRatio);
            // 正常流量不会超过容量
            edge.setFlow(Math.min(normalFlow, edge.getCapacity()));
        }
    }

    /**
     * 生成攻击流量
     * 在源点出边上生成大量攻击流量
     */
    public void generateAttackTraffic(int attackIntensity) {
        Node source = graph.getSource();
        if (source == null) {
            return;
        }

        // 在源点的所有出边上生成攻击流量
        for (Edge edge : graph.getOutEdges(source)) {
            // 攻击流量可能超过容量（模拟DDoS攻击）
            int attackFlow = edge.getCapacity() + attackIntensity;
            edge.setFlow(Math.max(edge.getFlow(), attackFlow));
        }
    }

    /**
     * 生成随机流量模式
     */
    public void generateRandomTraffic(int minFlow, int maxFlow) {
        for (Edge edge : graph.getEdges()) {
            int randomFlow = minFlow + random.nextInt(maxFlow - minFlow + 1);
            edge.setFlow(Math.min(randomFlow, edge.getCapacity()));
        }
    }

    /**
     * 清空所有流量
     */
    public void clearTraffic() {
        graph.resetFlow();
    }
}

