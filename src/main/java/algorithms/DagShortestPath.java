package algorithms;

import graph.Graph;
import java.util.Arrays;
import java.util.List;

public class DagShortestPath {

    public static class PathResult {
        public long sumOfDistances;
        public long edgeScans;
        public int[] dist;
        public int[] parent;

        public PathResult(long sumOfDistances, long edgeScans, int[] dist, int[] parent) {
            this.sumOfDistances = sumOfDistances;
            this.edgeScans = edgeScans;
            this.dist = dist;
            this.parent = parent;
        }
    }

    public static PathResult compute(Graph graph, List<Integer> topoOrder) {
        int n = graph.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE / 2); // Защита от переполнения при сложении
        Arrays.fill(parent, -1);

        // Все вершины с in-degree 0 (или все стартовые) получают расстояние 0 (виртуальный супер-источник)[cite: 2]
        // На практике в сгенерированных DAG можно инициализировать все вершины с indegree == 0 или просто все, у кого нет входящих
        int[] inDegree = new int[n];
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getAdj().get(u)) {
                inDegree[edge.v]++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                dist[i] = 0;
            }
        }

        long edgeScans = 0;

        // Релаксация ребер в топологическом порядке[cite: 2]
        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE / 2) {
                for (Graph.Edge edge : graph.getAdj().get(u)) {
                    edgeScans++;
                    int v = edge.v;
                    int weight = edge.weight;
                    if (dist[u] + weight < dist[v]) {
                        dist[v] = dist[u] + weight;
                        parent[v] = u;
                    }
                }
            }
        }

        long sumOfDistances = 0;
        for (int i = 0; i < n; i++) {
            if (dist[i] < Integer.MAX_VALUE / 2) {
                sumOfDistances += dist[i];
            }
        }

        return new PathResult(sumOfDistances, edgeScans, dist, parent);
    }
}