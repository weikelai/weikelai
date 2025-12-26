package com.myblog.firstjavaproject.javawork;
import java.util.*;
import java.io.*;

/**
 * MapRouting.java
 * @Author: 赖国燕
 * @CreateTime: 2025-12-06
 * @Description:
 * @Version: 1.0
 * 功能：
 * - 构建包含 distance, congestion, time 的无向图
 * - Kruskal 求最小生成树（以 distance 为权重）
 * - Bellman-Ford 求最短距离路径
 * - Dijkstra 求最不拥堵路径（以 congestion 为权重）
 * - Dijkstra 求最短时间路径（以 time 为权重）
 *
 * 使用示例：
 * 运行程序，按提示输入城市数、城市名、边数、每条边（cityA cityB distance congestion time）
 * 然后输入要查询的源和目标城市，程序会输出三种路径。
 */
public class MapRouting {

    static class Edge {
        int u, v;
        double distance;
        double congestion;
        double time;
        Edge(int u, int v, double distance, double congestion, double time) {
            this.u = u; this.v = v;
            this.distance = distance;
            this.congestion = congestion;
            this.time = time;
        }
    }

    static class Graph {
        int n; // 节点数
        ArrayList<Edge> edges = new ArrayList<>();
        ArrayList<List<Edge>> adj;
        Graph(int n) {
            this.n = n;
            adj = new ArrayList<>(n);
            for (int i = 0; i < n; ++i) adj.add(new ArrayList<>());
        }
        void addUndirectedEdge(int u, int v, double distance, double congestion, double time) {
            Edge e = new Edge(u, v, distance, congestion, time);
            edges.add(e);
            adj.get(u).add(e);
            Edge rev = new Edge(v, u, distance, congestion, time);
            adj.get(v).add(rev);
        }
    }

    // Kruskal
    static class UnionFind {
        int[] p;
        UnionFind(int n) { p = new int[n]; for (int i=0;i<n;i++) p[i]=i; }
        int find(int x) { return p[x]==x?x:(p[x]=find(p[x])); }
        boolean union(int a, int b) {
            int pa = find(a), pb = find(b);
            if (pa==pb) return false;
            p[pb] = pa;
            return true;
        }
    }

    static void kruskalMST(int n, List<Edge> undirectedEdges, Map<Integer,String> idxToName) {
        List<Edge> list = new ArrayList<>(undirectedEdges);
        list.sort(Comparator.comparingDouble(e -> e.distance));
        UnionFind uf = new UnionFind(n);
        double total = 0;
        List<Edge> chosen = new ArrayList<>();
        for (Edge e : list) {
            if (uf.union(e.u, e.v)) {
                chosen.add(e);
                total += e.distance;
            }
            if (chosen.size() == n-1) break;
        }
        System.out.println("\n--- 最小生成树 (Kruskal, 以距离为权重) ---");
        if (chosen.size() < n-1) {
            System.out.println("图不连通，无法生成完整 MST。已选边：");
        }
        for (Edge e : chosen) {
            System.out.printf("%s -- %s  dist=%.3f\n", idxToName.get(e.u), idxToName.get(e.v), e.distance);
        }
        System.out.printf("MST 总距离 = %.3f\n", total);
    }

    // Bellman-Ford
    static class BFResult {
        boolean hasNegativeCycle;
        double[] dist;
        int[] pred;
        BFResult(boolean hasNegativeCycle, double[] dist, int[] pred) {
            this.hasNegativeCycle = hasNegativeCycle; this.dist = dist; this.pred = pred;
        }
    }

    static BFResult bellmanFord(int n, List<Edge> directedEdges, int source) {
        double INF = Double.POSITIVE_INFINITY;
        double[] dist = new double[n];
        int[] pred = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(pred, -1);
        dist[source] = 0;

        for (int i=0;i<n-1;i++) {
            boolean changed = false;
            for (Edge e : directedEdges) {
                if (dist[e.u] != INF && dist[e.u] + e.distance < dist[e.v]) {
                    dist[e.v] = dist[e.u] + e.distance;
                    pred[e.v] = e.u;
                    changed = true;
                }
            }
            if (!changed) break;
        }
        // detect negative cycle
        for (Edge e : directedEdges) {
            if (dist[e.u] != INF && dist[e.u] + e.distance < dist[e.v]) {
                return new BFResult(true, dist, pred);
            }
        }
        return new BFResult(false, dist, pred);
    }

    // Dijkstra
    static class DijkstraResult {
        double[] dist;
        int[] pred;
        DijkstraResult(double[] dist, int[] pred) { this.dist = dist; this.pred = pred; }
    }

    static DijkstraResult dijkstra(Graph g, int source, java.util.function.ToDoubleFunction<Edge> weightFunc) {
        int n = g.n;
        double INF = Double.POSITIVE_INFINITY;
        double[] dist = new double[n];
        int[] pred = new int[n];
        Arrays.fill(dist, INF);
        Arrays.fill(pred, -1);
        dist[source] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1])); // [node, dist as double cast]
        pq.add(new int[]{source, 0});
        class Node implements Comparable<Node> {
            int v; double d;
            Node(int v, double d) { this.v=v; this.d=d; }
            public int compareTo(Node o) { return Double.compare(this.d, o.d); }
        }
        PriorityQueue<Node> q = new PriorityQueue<>();
        q.add(new Node(source, 0.0));

        boolean[] visited = new boolean[n];
        while (!q.isEmpty()) {
            Node cur = q.poll();
            int u = cur.v;
            if (visited[u]) continue;
            visited[u] = true;
            for (Edge e : g.adj.get(u)) {
                int v = e.v;
                double w = weightFunc.applyAsDouble(e);
                if (w < 0) continue;
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    pred[v] = u;
                    q.add(new Node(v, dist[v]));
                }
            }
        }
        return new DijkstraResult(dist, pred);
    }

    static List<Integer> reconstructPath(int[] pred, int target) {
        LinkedList<Integer> path = new LinkedList<>();
        int cur = target;
        while (cur != -1) {
            path.addFirst(cur);
            cur = pred[cur];
        }
        return path;
    }


    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== 城市道路规划与路径查询 ===");
        System.out.print("请输入城市数量 n: ");
        int n = Integer.parseInt(sc.nextLine().trim());
        Map<String, Integer> nameToIdx = new HashMap<>();
        Map<Integer, String> idxToName = new HashMap<>();
        System.out.println("请输入 " + n + " 个城市名（每行一个）：");
        for (int i = 0; i < n; ++i) {
            String name = sc.nextLine().trim();
            nameToIdx.put(name, i);
            idxToName.put(i, name);
        }

        System.out.print("请输入道路数量 m: ");
        int m = Integer.parseInt(sc.nextLine().trim());
        Graph g = new Graph(n);
        List<Edge> undirectedEdgesForMST = new ArrayList<>();

        System.out.println("请输入每条道路 (格式: cityA cityB distance congestion time) ：");
        for (int i=0;i<m;i++) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) { i--; continue; }
            String[] parts = line.split("\\s+");
            if (parts.length < 5) {
                System.out.println("输入格式错误，请重输该条：");
                i--; continue;
            }
            String a = parts[0], b = parts[1];
            if (!nameToIdx.containsKey(a) || !nameToIdx.containsKey(b)) {
                System.out.println("城市名不存在，请重输该条：");
                i--; continue;
            }
            int u = nameToIdx.get(a), v = nameToIdx.get(b);
            double distance = Double.parseDouble(parts[2]);
            double congestion = Double.parseDouble(parts[3]);
            double time = Double.parseDouble(parts[4]);
            g.addUndirectedEdge(u, v, distance, congestion, time);
            if (u < v) undirectedEdgesForMST.add(new Edge(u, v, distance, congestion, time));
            else undirectedEdgesForMST.add(new Edge(v, u, distance, congestion, time));
        }

        kruskalMST(n, undirectedEdgesForMST, idxToName);


        List<Edge> directedEdges = new ArrayList<>();
        for (Edge e : undirectedEdgesForMST) {
            directedEdges.add(new Edge(e.u, e.v, e.distance, e.congestion, e.time));
            directedEdges.add(new Edge(e.v, e.u, e.distance, e.congestion, e.time));
        }

        System.out.println("\n请输入查询：源 目标（城市名），例如: A B");
        System.out.print("源城市: ");
        String srcName = sc.nextLine().trim();
        System.out.print("目标城市: ");
        String dstName = sc.nextLine().trim();
        if (!nameToIdx.containsKey(srcName) || !nameToIdx.containsKey(dstName)) {
            System.out.println("源或目标城市不存在，退出。");
            return;
        }
        int src = nameToIdx.get(srcName), dst = nameToIdx.get(dstName);

        // 1) Bellman-Ford 最短距离（distance）
        BFResult bfRes = bellmanFord(n, directedEdges, src);
        if (bfRes.hasNegativeCycle) {
            System.out.println("检测到负权回路，无法可靠计算最短距离。");
        } else {
            double d = bfRes.dist[dst];
            System.out.println("\n---- 最短距离 (Bellman-Ford) ----");
            if (Double.isInfinite(d)) {
                System.out.println("从 " + srcName + " 到 " + dstName + " 无可达路径（按 distance）。");
            } else {
                List<Integer> path = reconstructPath(bfRes.pred, dst);
                System.out.print("路径: ");
                for (int i=0;i<path.size();i++) {
                    System.out.print(idxToName.get(path.get(i)) + (i+1<path.size()? " -> ":""));
                }
                System.out.printf("\n总距离 = %.3f\n", d);
            }
        }

        // 2) 最不拥堵路径（congestion） 使用 Dijkstra（假设拥塞值 >= 0）
        DijkstraResult congRes = dijkstra(g, src, e -> e.congestion);
        System.out.println("\n---- 最不拥堵路径 (Dijkstra, 以 congestion 为权重) ----");
        if (Double.isInfinite(congRes.dist[dst])) {
            System.out.println("从 " + srcName + " 到 " + dstName + " 无可达路径（按 congestion）。");
        } else {
            List<Integer> path = reconstructPath(congRes.pred, dst);
            System.out.print("路径: ");
            for (int i=0;i<path.size();i++) {
                System.out.print(idxToName.get(path.get(i)) + (i+1<path.size()? " -> ":""));
            }
            System.out.printf("\n总拥塞代价 = %.3f\n", congRes.dist[dst]);
        }

        // 3) 时间最短路径（time） 使用 Dijkstra（假设 time >= 0）
        DijkstraResult timeRes = dijkstra(g, src, e -> e.time);
        System.out.println("\n---- 时间最短路径 (Dijkstra, 以 time 为权重) ----");
        if (Double.isInfinite(timeRes.dist[dst])) {
            System.out.println("从 " + srcName + " 到 " + dstName + " 无可达路径（按 time）。");
        } else {
            List<Integer> path = reconstructPath(timeRes.pred, dst);
            System.out.print("路径: ");
            for (int i=0;i<path.size();i++) {
                System.out.print(idxToName.get(path.get(i)) + (i+1<path.size()? " -> ":""));
            }
            System.out.printf("\n总耗时 = %.3f\n", timeRes.dist[dst]);
        }

        System.out.println("\n查询结束。");
    }
}
