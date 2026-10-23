package algorithms;

public class DSU {
    private int[] parent;
    private int[] rank;
    private int componentsCount; // количество несвязных компонент

    // Счетчики операций по требованиям лабы
    private long findOps = 0;
    private long unionOps = 0;

    // Конструктор: создаем n элементов, каждый в своем множестве
    public DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        componentsCount = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i; //значально каждый сам себе родитель
            rank[i] = 0;
        }
    }

    // Метод поиска корня с эвристикой сжатия пути (path compression)
    public int find(int x) {
        findOps++; // увеличиваем счетчик вызовов find
        if (x == parent[x]) {
            return x;
        }
        // Сжатие пути: подвешиваем вершину напрямую к корню
        return parent[x] = find(parent[x]);
    }

    // Метод объединения двух множеств с эвристикой по рангу (union by rank)
    // Возвращает true, если объединение произошло, и false, если они уже были в одном множестве
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX != rootY) {
            // Эвристика по рангу: присоединяем меньшее дерево к большему
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
            componentsCount--; // количество компонент уменьшилось
            unionOps++;        // увеличиваем счетчик успешных union
            return true;
        }
        return false;
    }

    // Геттеры для получения значений счетчиков и компонент
    public int getComponentsCount() {
        return componentsCount;
    }

    public long getFindOps() {
        return findOps;
    }

    public long getUnionOps() {
        return unionOps;
    }
}