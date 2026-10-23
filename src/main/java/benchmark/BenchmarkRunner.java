package benchmark;

import algorithms.*;
import graph.Graph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class BenchmarkRunner {

    // Точный генератор по спецификации лабы (seed = 42)
    public static Graph generateGraph(int n, int m, String kind) {
        Random rnd = new Random(42);
        boolean isDirected = !kind.equals("undirected");
        Graph graph = new Graph(n, isDirected);

        if (kind.equals("undirected")) {
            // Генерация случайного остовного дерева для связности[cite: 3]
            int[] perm = new int[n];
            for (int i = 0; i < n; i++) perm[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int temp = perm[i]; perm[i] = perm[j]; perm[j] = temp;
            }
            for (int i = 1; i < n; i++) {
                int parent = perm[rnd.nextInt(i)];
                int child = perm[i];
                int weight = 1 + rnd.nextInt(1000);
                graph.addEdge(parent, child, weight);
            }
            // Добавление оставшихся ребер
            while (graph.getAllEdges().size() < m) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                if (u != v) {
                    int weight = 1 + rnd.nextInt(1000);
                    graph.addEdge(u, v, weight);
                }
            }
        } else if (kind.equals("directed")) {
            while (graph.getAllEdges().size() < m) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                if (u != v) {
                    int weight = 1 + rnd.nextInt(1000);
                    graph.addEdge(u, v, weight);
                }
            }
        } else if (kind.equals("dag")) {
            int[] rank = new int[n];
            for (int i = 0; i < n; i++) rank[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int temp = rank[i]; rank[i] = rank[j]; rank[j] = temp;
            }
            while (graph.getAllEdges().size() < m) {
                int u = rnd.nextInt(n);
                int v = rnd.nextInt(n);
                if (u != v) {
                    int weight = 1 + rnd.nextInt(1000);
                    if (rank[u] > rank[v]) {
                        int tmp = u; u = v; v = tmp; // гарантируем ацикличность по рангу[cite: 3]
                    }
                    graph.addEdge(u, v, weight);
                }
            }
        }
        return graph;
    }

    public static void runBenchmark() throws IOException {
        int[] sizes = {1000, 3162, 10000, 31623, 100000};
        String[] densities = {"sparse", "dense"};

        try (PrintWriter pw = new PrintWriter(new FileWriter("results/results.csv"))) {
            pw.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");

            for (int n : sizes) {
                for (String density : densities) {
                    int m = density.equals("sparse") ? 4 * n : 20 * n;

                    // 1. Undirected для MST (Kruskal, Prim)
                    Graph gUndir = generateGraph(n, m, "undirected");

                    long start = System.currentTimeMillis();
                    MST.MSTResult kruskalRes = MST.kruskal(gUndir);
                    long timeKruskal = System.currentTimeMillis() - start;
                    pw.printf("Kruskal,%s,%d,%d,%d,%d,%d,%d,0,%d\n", density, n, m, timeKruskal, kruskalRes.dsuOps, kruskalRes.edgeScans, kruskalRes.comparisons, kruskalRes.totalWeight);

                    start = System.currentTimeMillis();
                    MST.MSTResult primRes = MST.prim(gUndir);
                    long timePrim = System.currentTimeMillis() - start;
                    pw.printf("Prim,%s,%d,%d,%d,0,%d,0,0,%d\n", density, n, m, timePrim, primRes.edgeScans, primRes.totalWeight);

                    // 2. Directed для Kosaraju
                    Graph gDir = generateGraph(n, m, "directed");
                    start = System.currentTimeMillis();
                    GraphAlgorithms.SCCResult sccRes = GraphAlgorithms.kosaraju(gDir);
                    long timeKos = System.currentTimeMillis() - start;
                    pw.printf("Kosaraju,%s,%d,%d,%d,0,%d,0,%d,%d\n", density, n, m, timeKos, sccRes.edgeScans, sccRes.maxDepth, sccRes.sccCount);

                    // 3. DAG для Kahn и DagShortestPath
                    Graph gDag = generateGraph(n, m, "dag");
                    start = System.currentTimeMillis();
                    GraphAlgorithms.KahnResult kahnRes = GraphAlgorithms.kahn(gDag);
                    long timeKahn = System.currentTimeMillis() - start;
                    pw.printf("Kahn,%s,%d,%d,%d,0,%d,0,0,%d\n", density, n, m, timeKahn, kahnRes.edgeScans, kahnRes.order.size());

                    start = System.currentTimeMillis();
                    DagShortestPath.PathResult pathRes = DagShortestPath.compute(gDag, kahnRes.order);
                    long timePath = System.currentTimeMillis() - start;
                    pw.printf("DagShortestPath,%s,%d,%d,%d,0,%d,0,0,%d\n", density, n, m, timePath, pathRes.edgeScans, pathRes.sumOfDistances);
                }
            }
        }
        System.out.println("Бенчмарк успешно выполнен! Результаты записаны в results/results.csv");
    }

    public static void main(String[] args) throws IOException {
        runBenchmark();
    }
}