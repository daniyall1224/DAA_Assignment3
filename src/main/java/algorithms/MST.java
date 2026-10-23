package algorithms;

import graph.Graph;
import java.util.*;

public class MST {

    // Вспомогательный класс для хранения результатов работы алгоритмов MST
    public static class MSTResult {
        public int totalWeight;
        public long edgeScans;
        public long comparisons;
        public long dsuOps;

        public MSTResult(int totalWeight, long edgeScans, long comparisons, long dsuOps) {
            this.totalWeight = totalWeight;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
            this.dsuOps = dsuOps;
        }
    }

    // 1. Алгоритм Крускала (использует DSU)
    public static MSTResult kruskal(Graph graph) {
        List<Graph.Edge> edges = new ArrayList<>(graph.getAllEdges());
        long edgeScans = 0;

        // Сортировка ребер по весу с подсчетом сравнений (comparisons)
        final long[] compCount = {0};
        edges.sort((e1, e2) -> {
            compCount[0]++;
            return Integer.compare(e1.weight, e2.weight);
        });
        long comparisons = compCount[0];

        DSU dsu = new DSU(graph.size());
        int totalWeight = 0;

        for (Graph.Edge edge : edges) {
            edgeScans++; // Считаем просмотренные ребра
            int rootU = dsu.find(edge.u);
            int rootV = dsu.find(edge.v);

            if (rootU != rootV) {
                dsu.union(edge.u, edge.v);
                totalWeight += edge.weight;
            }
        }

        long dsuOps = dsu.getFindOps() + dsu.getUnionOps();
        return new MSTResult(totalWeight, edgeScans, comparisons, dsuOps);
    }

    // 2. Алгоритм Прима (использует PriorityQueue)
    public static MSTResult prim(Graph graph) {
        int n = graph.size();
        boolean[] visited = new boolean[n];

        // Очередь с приоритетами для выбора ребра с минимальным весом
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));

        long edgeScans = 0;
        long comparisons = 0;
        int totalWeight = 0;
        int edgesAdded = 0;

        // Начинаем с вершины 0 по условиям лабы
        if (n > 0) {
            visited[0] = true;
            for (Graph.Edge edge : graph.getAdj().get(0)) {
                edgeScans++;
                pq.add(edge);
            }
        }

        while (!pq.isEmpty() && edgesAdded < n - 1) {
            Graph.Edge edge = pq.poll();
            edgeScans++;

            int u = edge.u;
            int v = edge.v;

            // Определяем, какая вершина еще не посещена
            int nextVertex = visited[u] ? v : u;

            if (!visited[nextVertex]) {
                visited[nextVertex] = true;
                totalWeight += edge.weight;
                edgesAdded++;

                // Добавляем все инцидентные ребра новой вершины
                for (Graph.Edge nextEdge : graph.getAdj().get(nextVertex)) {
                    edgeScans++;
                    pq.add(nextEdge);
                }
            }
        }

        return new MSTResult(totalWeight, edgeScans, comparisons, 0);
    }
}