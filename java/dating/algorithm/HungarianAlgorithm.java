package dating.algorithm;

/**
 * 匈牙利算法实现 - 用于二分图最大匹配
 * 
 * 算法原理：
 * 1. 从左侧未匹配节点开始，尝试找到增广路径
 * 2. 如果找到增广路径，则增加匹配数
 * 3. 重复直到无法找到更多增广路径
 * 
 * @author Dating Match System
 */
public class HungarianAlgorithm {

    private int[][] graph; // 二分图的邻接矩阵，graph[i][j] = 1 表示左侧节点i与右侧节点j有边
    private int[] match; // match[j] = i 表示右侧节点j匹配到左侧节点i，-1表示未匹配
    private boolean[] used; // 用于DFS搜索时标记节点是否已访问
    private int leftSize; // 左侧节点数量
    private int rightSize; // 右侧节点数量

    /**
     * 构造函数
     * 
     * @param graph     二分图的邻接矩阵
     * @param leftSize  左侧节点数量
     * @param rightSize 右侧节点数量
     */
    public HungarianAlgorithm(int[][] graph, int leftSize, int rightSize) {
        this.graph = graph;
        this.leftSize = leftSize;
        this.rightSize = rightSize;
        this.match = new int[rightSize];
        this.used = new boolean[rightSize];

        // 初始化匹配数组
        for (int i = 0; i < rightSize; i++) {
            match[i] = -1;
        }
    }

    /**
     * 执行匈牙利算法，返回最大匹配数
     * 
     * @return 最大匹配数
     */
    public int findMaxMatching() {
        int result = 0;

        // 对每个左侧节点尝试匹配
        for (int i = 0; i < leftSize; i++) {
            // 重置used数组
            for (int j = 0; j < rightSize; j++) {
                used[j] = false;
            }

            // 如果找到增广路径，匹配数加1
            if (dfs(i)) {
                result++;
            }
        }

        return result;
    }

    /**
     * 深度优先搜索寻找增广路径
     * 
     * @param u 当前左侧节点
     * @return 是否找到增广路径
     */
    private boolean dfs(int u) {
        // 遍历所有右侧节点
        for (int v = 0; v < rightSize; v++) {
            // 如果存在边且右侧节点未被访问
            if (graph[u][v] == 1 && !used[v]) {
                used[v] = true;

                // 如果右侧节点未匹配，或者可以找到替代匹配
                if (match[v] == -1 || dfs(match[v])) {
                    match[v] = u; // 建立匹配
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取匹配结果
     * 
     * @return 匹配数组，match[j] = i 表示右侧节点j匹配到左侧节点i
     */
    public int[] getMatchResult() {
        return match.clone();
    }

    /**
     * 获取匹配对列表
     * 
     * @return 匹配对数组，每个元素为[leftIndex, rightIndex]
     */
    public int[][] getMatchPairs() {
        int count = 0;
        for (int i = 0; i < rightSize; i++) {
            if (match[i] != -1) {
                count++;
            }
        }

        int[][] pairs = new int[count][2];
        int index = 0;
        for (int i = 0; i < rightSize; i++) {
            if (match[i] != -1) {
                pairs[index][0] = match[i];
                pairs[index][1] = i;
                index++;
            }
        }

        return pairs;
    }
}
