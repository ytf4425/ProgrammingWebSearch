package pkuss.programmingweb.controller;

import pkuss.programmingweb.entity.API;
import pkuss.programmingweb.entity.Mashup;

import java.util.*;

public class Graph {
    Data data;
    private API vertices[];   // 点的数组
    private byte[][] unweightedMatrix; // 无权的邻接矩阵
    private byte[][] Matrix; // 带权的邻接矩阵

    Graph(Data data) {
        this.data = data;
        initData();
    }

    void initData() {
        ArrayList<API> vertexList = new ArrayList<>(data.getAPImap().values());
        API[] vertices = vertexList.toArray(new API[0]);

        this.vertices = vertices;
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].setIndexInGraph(i);
        }

        Matrix = new byte[vertices.length][vertices.length];
        unweightedMatrix = new byte[vertices.length][vertices.length];
        for (byte[] line : unweightedMatrix) {
            Arrays.fill(line, Byte.MAX_VALUE);
        }

        for (Mashup mashup : data.getMashupmap().values()) {
            List<API> apis = mashup.getApis();
            for (int i = 0; i < apis.size(); i++) {
                for (int j = i + 1; j < apis.size(); j++) {
                    addEdge(apis.get(i), apis.get(j));
                }
            }
        }
    }

//    Graph(Vertex[] vertices) {
//        this.vertices = vertices;
//        for (int i = 0; i < vertices.length; i++) {
//            vertices[i].setIndex(i);
//        }
//        matrix = new int[vertices.length][vertices.length];
//    }

    public void addEdge(API from, API to) {
        addEdge(from.getIndexInGraph(), to.getIndexInGraph());
    }

    public void addEdge(int from, int to) {
        Matrix[from][to] += 1;
        Matrix[to][from] += 1;
        unweightedMatrix[from][to] = 1;
        unweightedMatrix[to][from] = 1;
    }

    public int getEdgeWeight(API from, API to) {
        return getEdgeWeight(from.getIndexInGraph(), to.getIndexInGraph());
    }

    public int getEdgeWeight(int from, int to) {
        return Matrix[from][to];
    }

    public Set<Set<API>> search(String[] keywords, type t, InvertedIndex ii) {
        List<Set<API>> apiSets = new ArrayList<>();
        for (String keyword : keywords) {
            apiSets.add(ii.search(keyword, t));
        }

        Set<Set<API>> results = new HashSet<>();
        dfs(new HashSet<>(), apiSets, results, 0);
        return results;
    }

    private void dfs(Set<API> oneResult, List<Set<API>> apiSets, Set<Set<API>> results, int layer) {
        if (apiSets.size() == layer) {
            results.add(new HashSet<>(oneResult));
            return;
        }

        for (API api : apiSets.get(layer)) {
            if (!checkConnection(api, oneResult)) {
                continue;
            }
            oneResult.add(api);
            dfs(oneResult, apiSets, results, layer + 1);
            oneResult.remove(api);
        }
    }

    private boolean checkConnection(API api, Set<API> oneResult) {
        for (API in : oneResult) {
            if (getEdgeWeight(api, in) == 0 && !api.equals(in)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (byte[] lines : Matrix) {
            for (int weight : lines) {
//                if (weight != 0)
                ret.append(weight + "\t");
            }
            ret.append("\n");
        }
        return ret.toString();
    }
}
