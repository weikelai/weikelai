package graph;

import entity.Edge;
import entity.Node;

import java.util.*;

/**
 * 残余网络
 * 用于最大流算法中的增广路径搜索
 */
public class ResidualGraph {
    private Map<Node, List<ResidualEdge>> adjacencyList;
    private Graph originalGraph;

    /**
     * 残余边类
     */
    public static class ResidualEdge {
        private Node to;
        private int residualCapacity;
        private Edge originalEdge;
        private boolean isForward;  // true表示正向边，false表示反向边

        public ResidualEdge(Node to, int residualCapacity, Edge originalEdge, boolean isForward) {
            this.to = to;
            this.residualCapacity = residualCapacity;
            this.originalEdge = originalEdge;
            this.isForward = isForward;
        }

        public Node getTo() {
            return to;
        }

        public int getResidualCapacity() {
            return residualCapacity;
        }

        public Edge getOriginalEdge() {
            return originalEdge;
        }

        public boolean isForward() {
            return isForward;
        }
    }

    public ResidualGraph(Graph graph) {
        this.originalGraph = graph;
        this.adjacencyList = new HashMap<>();
        buildResidualGraph();
    }

    /**
     * 构建残余网络
     */
    private void buildResidualGraph() {
        // 初始化邻接表
        for (Node node : originalGraph.getNodes()) {
            adjacencyList.put(node, new ArrayList<>());
        }

        // 为每条边添加正向和反向边
        for (Edge edge : originalGraph.getEdges()) {
            Node from = edge.getFrom();
            Node to = edge.getTo();
            int residual = edge.getResidualCapacity();
            int flow = edge.getFlow();

            // 正向边：残余容量 = 容量 - 流量
            if (residual > 0) {
                adjacencyList.get(from).add(
                    new ResidualEdge(to, residual, edge, true)
                );
            }

            // 反向边：残余容量 = 当前流量（允许回流）
            if (flow > 0) {
                adjacencyList.get(to).add(
                    new ResidualEdge(from, flow, edge, false)
                );
            }
        }
    }

    /**
     * 获取节点的所有出边（在残余网络中）
     */
    public List<ResidualEdge> getOutEdges(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    /**
     * 使用BFS查找增广路径
     * @return 增广路径，如果不存在则返回null
     */
    public List<ResidualEdge> findAugmentingPath(Node source, Node sink) {
        Map<Node, Node> parentNode = new HashMap<>();  // 存储父节点
        Map<Node, ResidualEdge> parentEdge = new HashMap<>();  // 存储到达该节点的边
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.offer(source);
        visited.add(source);
        parentNode.put(source, null);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // 如果到达汇点，构建路径
            if (current.equals(sink)) {
                return buildPath(parentNode, parentEdge, source, sink);
            }

            // 遍历所有出边
            for (ResidualEdge edge : getOutEdges(current)) {
                Node next = edge.getTo();
                if (!visited.contains(next) && edge.getResidualCapacity() > 0) {
                    visited.add(next);
                    parentNode.put(next, current);
                    parentEdge.put(next, edge);
                    queue.offer(next);
                }
            }
        }

        return null; // 未找到增广路径
    }

    /**
     * 构建从源点到汇点的路径
     */
    private List<ResidualEdge> buildPath(Map<Node, Node> parentNode, Map<Node, ResidualEdge> parentEdge, Node source, Node sink) {
        List<ResidualEdge> path = new ArrayList<>();
        Node current = sink;

        while (current != null && !current.equals(source)) {
            ResidualEdge edge = parentEdge.get(current);
            if (edge == null) {
                return null;
            }
            path.add(0, edge);
            current = parentNode.get(current);  // 获取父节点
        }

        return path;
    }

    /**
     * 更新残余网络（在流量更新后调用）
     */
    public void update() {
        buildResidualGraph();
    }
}

