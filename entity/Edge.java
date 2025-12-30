package entity;

/**
 * 网络边类
 * 表示网络中的连接，包含容量和流量信息
 */
public class Edge {
    private Node from;           // 起始节点
    private Node to;             // 目标节点
    private int capacity;        // 容量（带宽）
    private int flow;            // 当前流量
    private int cost;            // 阻断代价

    public Edge(Node from, Node to, int capacity) {
        this(from, to, capacity, 0, 1);
    }

    public Edge(Node from, Node to, int capacity, int cost) {
        this(from, to, capacity, 0, cost);
    }

    public Edge(Node from, Node to, int capacity, int flow, int cost) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = flow;
        this.cost = cost;
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * 获取残余容量
     */
    public int getResidualCapacity() {
        return capacity - flow;
    }

    /**
     * 检查边是否饱和
     */
    public boolean isSaturated() {
        return flow >= capacity;
    }

    @Override
    public String toString() {
        return String.format("Edge{%s -> %s, capacity=%d, flow=%d, residual=%d, cost=%d}",
                from.getName(), to.getName(), capacity, flow, getResidualCapacity(), cost);
    }
}

