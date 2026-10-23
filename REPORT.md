# Report: Assignment 3 - Graphs (DSU, MST, SCC, Shortest Paths)

## 1. Complexity Table
| Algorithm | Time Complexity | Extra Space | Justification |
| :--- | :--- | :--- | :--- |
| **DSU (find/union)** | $O(\alpha(n))$ | $O(n)$ | Path compression and union by rank keep trees nearly flat. |
| **Kruskal** | $O(E \log E)$ | $O(V + E)$ | Dominated by sorting the edges by weight. |
| **Prim** | $O(E \log V)$ | $O(V + E)$ | Using a PriorityQueue to extract minimum weight edges. |
| **Kosaraju (SCC)** | $O(V + E)$ | $O(V + E)$ | Two passes of iterative DFS with an explicit stack. |
| **Kahn (Topological)** | $O(V + E)$ | $O(V + E)$ | In-degrees tracking and queue processing. |
| **DAG Shortest Paths** | $O(V + E)$ | $O(V + E)$ | Single pass of edge relaxations in topological order. |

## 2. Counters vs Formulas
We divided measured operation counters by theoretical formulas (e.g., `edgeScans / (V + E)` for Kosaraju) and verified that the ratio remains constant as $n$ grows, confirming linear complexity empirically[cite: 4, 5].

## 3. Correctness Arguments
* **Cut Property:** The lightest edge across any cut of a graph is always safe to include in a Minimum Spanning Tree. Both Kruskal and Prim rely on this property to greedily build the MST[cite: 4].
* **Induction on Topological Order:** In a DAG, processing vertices in topological order guarantees that all incoming paths to a vertex are fully relaxed before the vertex itself is used to relax outgoing edges, making a single $O(V+E)$ pass sufficient[cite: 4].

## 4. Which Algorithm When
| Graph Type | Requirement | Recommended Algorithm | Why? |
| :--- | :--- | :--- | :--- |
| Sparse, Undirected | Minimum Spanning Tree | Kruskal | Faster when edges are sparse and easy to sort. |
| Dense, Undirected | Minimum Spanning Tree | Prim | Better performance on dense graphs using adjacency lists and priority queues. |
| Directed | Strongly Connected Components | Kosaraju / Tarjan | Efficient two-pass or single-pass linear time traversal. |
| DAG | Shortest / Longest Paths | DAG Shortest Paths | Single-pass relaxation in $O(V+E)$ without expensive Dijkstra/Bellman-Ford overhead. |