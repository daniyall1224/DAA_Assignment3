package algorithms;

import graph.Graph;
import java.util.*;

public class GraphAlgorithms {

    public static class SCCResult {
        public int sccCount;
        public long edgeScans;
        public long maxDepth;

        public SCCResult(int sccCount, long edgeScans, long maxDepth) {
            this.sccCount = sccCount;
            this.edgeScans = edgeScans;
            this.maxDepth = maxDepth;
        }
    }

    public static class KahnResult {
        public List<Integer> order;
        public boolean hasCycle;
        public long edgeScans;

        public KahnResult(List<Integer> order, boolean hasCycle, long edgeScans) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.edgeScans = edgeScans;
        }
    }

    // 1. Алгоритм Косараджу (итеративный DFS с явным стеком)
    public static SCCResult kosaraju(Graph graph) {
        int n = graph.size();
        List<List<Graph.Edge>> adj = graph.getAdj();

        boolean[] visited = new boolean[n];
        ArrayDeque<Integer> finishOrder = new ArrayDeque<>();
        long edgeScans = 0;
        long maxDepth = 0;

        // Первый проход DFS для получения порядка завершения
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                // Имитация рекурсивного DFS через собственный стек
                ArrayDeque<Integer> stack = new ArrayDeque<>();
                ArrayDeque<Integer> stateStack = new ArrayDeque<>(); // хранит индекс следующего соседа

                stack.push(i);
                stateStack.push(0);
                visited[i] = true;

                while (!stack.isEmpty()) {
                    if (stack.size() > maxDepth) {
                        maxDepth = stack.size();
                    }
                    int u = stack.peek();
                    int neighborIdx = stateStack.pop();

                    List<Graph.Edge> edges = adj.get(u);
                    if (neighborIdx < edges.size()) {
                        stateStack.push(neighborIdx + 1);
                        Graph.Edge edge = edges.get(neighborIdx);
                        edgeScans++;
                        int v = edge.v;
                        if (!visited[v]) {
                            visited[v] = true;
                            stack.push(v);
                            stateStack.push(0);
                        }
                    } else {
                        stack.pop();
                        finishOrder.push(u);
                    }
                }
            }
        }

        // Построение обратного графа (reversed graph)
        List<List<Integer>> revAdj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) revAdj.add(new ArrayList<>());
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : adj.get(u)) {
                revAdj.get(edge.v).add(u);
            }
        }

        // Второй проход DFS по обратному графу в порядке убывания времени завершения
        Arrays.fill(visited, false);
        int sccCount = 0;

        for (int start : finishOrder) {
            if (!visited[start]) {
                sccCount++;
                ArrayDeque<Integer> stack = new ArrayDeque<>();
                stack.push(start);
                visited[start] = true;

                while (!stack.isEmpty()) {
                    int u = stack.pop();
                    for (int v : revAdj.get(u)) {
                        edgeScans++; // Считаем просмотренные ребра во втором проходе
                        if (!visited[v]) {
                            visited[v] = true;
                            stack.push(v);
                        }
                    }
                }
            }
        }

        return new SCCResult(sccCount, edgeScans, maxDepth);
    }

    // 2. Алгоритм Кана (топологическая сортировка и поиск циклов)[cite: 2]
    public static KahnResult kahn(Graph graph) {
        int n = graph.size();
        int[] inDegree = new int[n];
        long edgeScans = 0;

        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getAdj().get(u)) {
                edgeScans++;
                inDegree[edge.v]++;
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            for (Graph.Edge edge : graph.getAdj().get(u)) {
                int v = edge.v;
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.add(v);
                }
            }
        }

        boolean hasCycle = (order.size() != n);
        return new KahnResult(order, hasCycle, edgeScans);
    }
}