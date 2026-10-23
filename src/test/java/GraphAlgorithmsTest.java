import algorithms.*;
import graph.Graph;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GraphAlgorithmsTest {

    @Test
    public void testDSU() {
        DSU dsu = new DSU(5);
        dsu.union(0, 1);
        dsu.union(1, 2);
        assertEquals(dsu.find(0), dsu.find(2));
        assertNotEquals(dsu.find(0), dsu.find(3));
        assertEquals(3, dsu.getComponentsCount());
    }

    @Test
    public void testMSTAgreement() {
        // Генерируем тестовый граф и проверяем, что Крускал и Прим дают одинаковый вес MST[cite: 2, 4]
        Graph graph = benchmark.BenchmarkRunner.generateGraph(200, 800, "undirected");
        MST.MSTResult kruskalRes = MST.kruskal(graph);
        MST.MSTResult primRes = MST.prim(graph);
        assertEquals(kruskalRes.totalWeight, primRes.totalWeight, "Kruskal and Prim MST weights must agree!");
    }

    @Test
    public void testStackSafetySCC() {
        // Проверка итеративного DFS на длинной цепи из 100 000 вершин (защита от StackOverflowError)[cite: 2, 4]
        int n = 100000;
        Graph graph = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) {
            graph.addEdge(i, i + 1, 1);
        }
        GraphAlgorithms.SCCResult res = GraphAlgorithms.kosaraju(graph);
        assertEquals(n, res.sccCount);
    }

    @Test
    public void testKahnAndDagPaths() {
        Graph graph = benchmark.BenchmarkRunner.generateGraph(100, 400, "dag");
        GraphAlgorithms.KahnResult kahnRes = GraphAlgorithms.kahn(graph);
        assertFalse(kahnRes.hasCycle, "DAG must not have cycles");

        DagShortestPath.PathResult pathRes = DagShortestPath.compute(graph, kahnRes.order);
        assertNotNull(pathRes);
        assertTrue(pathRes.sumOfDistances >= 0);
    }
}