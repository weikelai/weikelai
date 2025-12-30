package algorithm;

import graph.Graph;

/**
 * 最大流算法接口
 */
public interface MaxFlow {
    /**
     * 计算最大流
     * @param graph 网络图
     * @return 最大流值
     */
    int computeMaxFlow(Graph graph);

    /**
     * 获取算法名称
     */
    String getAlgorithmName();
}

