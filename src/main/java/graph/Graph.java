package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Graph {
    // Внутренний класс для хранения ребра графа (откуда, куда, вес)
    public static class Edge {
        public int u, v, weight;
        public Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }

    private final int n; // количество вершин
    private final boolean isDirected; // true, если граф ориентированный, false — если нет
    private final List<List<Edge>> adj; // список смежности
    private final List<Edge> allEdges; // список всех ребер

    public Graph(int n, boolean isDirected) {
        this.n = n;
        this.isDirected = isDirected;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        this.allEdges = new ArrayList<>();
    }

    // Метод добавления ребра
    public void addEdge(int u, int v, int weight) {
        Edge edge = new Edge(u, v, weight);
        allEdges.add(edge);
        adj.get(u).add(edge);
        if (!isDirected) {
            // Если граф неориентированный, добавляем обратное ребро
            Edge reverseEdge = new Edge(v, u, weight);
            adj.get(v).add(reverseEdge);
        }
    }

    public int size() { return n; }
    public boolean isDirected() { return isDirected; }
    public List<List<Edge>> getAdj() { return adj; }
    public List<Edge> getAllEdges() { return allEdges; }

    // Чтение графа из текстового файла
    public static Graph loadFromFile(String filepath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String[] firstLine = br.readLine().trim().split("\\s+");
            int n = Integer.parseInt(firstLine[0]);
            int m = Integer.parseInt(firstLine[1]);
            boolean isDirected = Boolean.parseBoolean(firstLine[2]);

            Graph graph = new Graph(n, isDirected);
            for (int i = 0; i < m; i++) {
                String[] edgeLine = br.readLine().trim().split("\\s+");
                int u = Integer.parseInt(edgeLine[0]);
                int v = Integer.parseInt(edgeLine[1]);
                int w = Integer.parseInt(edgeLine[2]);
                graph.addEdge(u, v, w);
            }
            return graph;
        }
    }
}