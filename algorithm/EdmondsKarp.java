package algorithm;

import entity.Edge;
import entity.Node;
import graph.Graph;
import graph.ResidualGraph;

import java.util.List;

/**
 * Edmonds-Karp 最大流算法实现
 * 使用BFS寻找最短增广路径，保证多项式时间复杂度
 * 时间复杂度: O(V * E^2)
 * 空间复杂度: O(V + E)
 */
public class EdmondsKarp implements MaxFlow {
    private int maxFlowValue;

    @Override
    public int computeMaxFlow(Graph graph) {
        this.maxFlowValue = 0;

        // 重置所有边的流量
        graph.resetFlow();

        Node source = graph.getSource();
        Node sink = graph.getSink();

        if (source == null || sink == null) {
            throw new IllegalArgumentException("图必须包含源点和汇点");
        }

        // 构建残余网络
        ResidualGraph residualGraph = new ResidualGraph(graph);

        // 不断寻找增广路径并更新流量
        while (true) {
            List<ResidualGraph.ResidualEdge> path = residualGraph.findAugmentingPath(source, sink);
            
            if (path == null || path.isEmpty()) {
                break; // 没有增广路径，算法结束
            }

            // 计算路径上的最小残余容量
            int minCapacity = findMinCapacity(path);

            // 更新路径上每条边的流量
            updateFlow(path, minCapacity);

            // 更新最大流值
            maxFlowValue += minCapacity;

            // 更新残余网络
            residualGraph.update();
        }

        return maxFlowValue;
    }

    /**
     * 找到路径上的最小残余容量
     */
    private int findMinCapacity(List<ResidualGraph.ResidualEdge> path) {
        int minCapacity = Integer.MAX_VALUE;
        for (ResidualGraph.ResidualEdge edge : path) {
            minCapacity = Math.min(minCapacity, edge.getResidualCapacity());
        }
        return minCapacity;
    }

    /**
     * 更新路径上每条边的流量
     */
    private void updateFlow(List<ResidualGraph.ResidualEdge> path, int flow) {
        for (ResidualGraph.ResidualEdge residualEdge : path) {
            Edge originalEdge = residualEdge.getOriginalEdge();
            
            if (residualEdge.isForward()) {
                // 正向边：增加流量
                originalEdge.setFlow(originalEdge.getFlow() + flow);
            } else {
                // 反向边：减少流量（回流）
                originalEdge.setFlow(originalEdge.getFlow() - flow);
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "Edmonds-Karp";
    }

    /**
     * 获取最大流值
     */
    public int getMaxFlowValue() {
        return maxFlowValue;
    }
}

