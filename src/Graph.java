import java.util.*;

public class Graph {
    private Map<Integer, List<Integer>> graph;

    public Graph() {
        this.graph = new HashMap<>();
    }

    public void addConnection(int begin, int end) {
        if (!graph.containsKey(begin)) {
            graph.put(begin, new ArrayList<>());
        }
        graph.get(begin).add(end);
    }

    public boolean hasLoop() {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> stack = new HashSet<>();

        for (int node : graph.keySet()) {
            if (dfs(node, visited, stack)) {
                return true;
            }
        }

        return false;
    }

    public boolean isConnectedGraph() {
        Set<Integer> visited = new HashSet<>();
        dfs(graph.keySet().iterator().next(), visited);
        return visited.size() == graph.size();
    }

    private void dfs(int node, Set<Integer> visited) {
        visited.add(node);
        List<Integer> neighbors = graph.get(node);
        if (neighbors != null) {
            for (Integer neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    dfs(neighbor, visited);
                }
            }
        }
    }

    private boolean dfs(int node, Set<Integer> visited, Set<Integer> stack) {
        if (stack.contains(node)) {
            return true;
        }
        if (visited.contains(node)) {
            return false;
        }

        stack.add(node);
        visited.add(node);

        List<Integer> neighbors = graph.get(node);
        if (neighbors != null) {
            for (Integer neighbor : neighbors) {
                if (dfs(neighbor, visited, stack)) {
                    return true;
                }
            }
        }

        stack.remove(node);
        return false;
    }

    public static void main(String[] args) {
        Graph graph = new Graph();

        graph.addConnection(1, 6);
        graph.addConnection(6, 2);
        graph.addConnection(2, 1);
        graph.addConnection(5, 7);

        System.out.println("Has Loop: " + graph.hasLoop()); // Output: true
        System.out.println("Is Connected Graph: " + graph.isConnectedGraph()); // Output: false
    }
}