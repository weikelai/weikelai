package entity;

/**
 * 网络节点类
 * 表示网络中的主机、路由器或服务器
 */
public class Node {
    private int id;              // 节点ID
    private String name;         // 节点名称
    private NodeType type;       // 节点类型

    public enum NodeType {
        SOURCE,    // 源点（攻击源）
        SINK,      // 汇点（目标服务器）
        ROUTER,    // 路由器
        HOST       // 普通主机
    }

    public Node(int id, String name, NodeType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("Node{id=%d, name='%s', type=%s}", id, name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

