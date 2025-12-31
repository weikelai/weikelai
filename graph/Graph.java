package graph;

import entity.Edge;
import entity.Node;

import java.util.*;

/**
 * 图结构定义
 * 使用邻接表存储网络拓扑
 */
public class Graph {
    private Map<Node, List<Edge>> adjacencyList;  // 邻接表
    private List<Node> nodes;                      // 所有节点
    private List<Edge> edges;                      // 所有边
    private Node source;                           // 源点
    private Node sink;                             // 汇点

    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    /**
     * 添加节点
     */
    public void addNode(Node node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            adjacencyList.put(node, new ArrayList<>());
            
            // 设置源点和汇点
            if (node.getType() == Node.NodeType.SOURCE) {
                source = node;
            } else if (node.getType() == Node.NodeType.SINK) {
                sink = node;
            }
        }
    }

    /**
     * 添加边
     */
    public void addEdge(Edge edge) {
        Node from = edge.getFrom();
        Node to = edge.getTo();
        
        // 确保节点已添加
        addNode(from);
        addNode(to);
        
        // 添加边到邻接表
        adjacencyList.get(from).add(edge);
        edges.add(edge);
    }

    /**
     * 添加边（便捷方法）
     */
    public void addEdge(Node from, Node to, int capacity) {
        addEdge(new Edge(from, to, capacity));
    }

    /**
     * 添加边（带代价）
     */
    public void addEdge(Node from, Node to, int capacity, int cost) {
        addEdge(new Edge(from, to, capacity, cost));
    }

    /**
     * 获取节点的所有出边
     */
    public List<Edge> getOutEdges(Node node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    /**
     * 获取节点的所有入边
     */
    public List<Edge> getInEdges(Node node) {
        List<Edge> inEdges = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getTo().equals(node)) {
                inEdges.add(edge);
            }
        }
        return inEdges;
    }

    /**
     * 获取所有节点
     */
    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }

    /**
     * 获取所有边
     */
    public List<Edge> getEdges() {
        return new ArrayList<>(edges);
    }

    /**
     * 获取源点
     */
    public Node getSource() {
        return source;
    }

    /**
     * 获取汇点
     */
    public Node getSink() {
        return sink;
    }

    /**
     * 获取节点数量
     */
    public int getNodeCount() {
        return nodes.size();
    }

    /**
     * 获取边数量
     */
    public int getEdgeCount() {
        return edges.size();
    }

    /**
     * 根据ID查找节点
     */
    public Node findNodeById(int id) {
        for (Node node : nodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }

    /**
     * 根据名称查找节点
     */
    public Node findNodeByName(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    /**
     * 清空所有流量
     */
    public void resetFlow() {
        for (Edge edge : edges) {
            edge.setFlow(0);
        }
    }

    /**
     * 保存所有边的原始容量
     * @return 容量映射表（边 -> 原始容量）
     */
    public Map<Edge, Integer> saveOriginalCapacities() {
        Map<Edge, Integer> originalCapacities = new HashMap<>();
        for (Edge edge : edges) {
            originalCapacities.put(edge, edge.getCapacity());
        }
        return originalCapacities;
    }

    /**
     * 恢复所有边的原始容量
     * @param originalCapacities 原始容量映射表
     */
    public void restoreOriginalCapacities(Map<Edge, Integer> originalCapacities) {
        for (Map.Entry<Edge, Integer> entry : originalCapacities.entrySet()) {
            entry.getKey().setCapacity(entry.getValue());
        }
    }

    /**
     * 打印图结构
     */
    public void printGraph() {
        System.out.println("=== 网络拓扑结构 ===");
        System.out.println("节点数量: " + getNodeCount());
        System.out.println("边数量: " + getEdgeCount());
        System.out.println("\n节点列表:");
        for (Node node : nodes) {
            System.out.println("  " + node);
        }
        System.out.println("\n边列表:");
        for (Edge edge : edges) {
            System.out.println("  " + edge);
        }
        System.out.println();
    }

    /**
     * 深拷贝图（节点与边均复制，流量置为0）
     */
    public Graph deepCopy() {
        Graph copy = new Graph();
        Map<Node, Node> nodeMap = new HashMap<>();

        // 复制节点
        for (Node node : this.nodes) {
            Node nodeCopy = new Node(node.getId(), node.getName(), node.getType());
            copy.addNode(nodeCopy);
            nodeMap.put(node, nodeCopy);
        }

        // 复制边（保持容量与代价，流量置0）
        for (Edge edge : this.edges) {
            Node fromCopy = nodeMap.get(edge.getFrom());
            Node toCopy = nodeMap.get(edge.getTo());
            Edge edgeCopy = new Edge(fromCopy, toCopy, edge.getCapacity(), 0, edge.getCost());
            copy.addEdge(edgeCopy);
        }

        return copy;
    }
}

