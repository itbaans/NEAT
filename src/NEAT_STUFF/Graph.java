package NEAT_STUFF;
import java.util.*;

public class Graph {
    Map<Integer, List<Integer>> graph;

    public Graph() {
        this.graph = new HashMap<>();
    }

    public void addConnection(int begin, int end) {

        if (!graph.containsKey(begin)) {
            graph.put(begin, new ArrayList<>());
        }
        if (!graph.containsKey(end)) {
            graph.put(end, new ArrayList<>());
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

    public boolean hasNoIsolatedNodes(Set<Integer> startNodes, Set<Integer> outNodes) {
        
        Set<Integer> allVisited = new HashSet<>();
        
        for (Integer startNode : startNodes) {
            Set<Integer> visited = new HashSet<>();  
            dfsTraversal(startNode, visited);
            allVisited.addAll(visited);
        }
            
        for (Integer node : graph.keySet()) {

            if(startNodes.contains(node)) {
                if(!allVisited.contains(node) || graph.get(node).isEmpty())
                    return false;
            }

            if(outNodes.contains(node)) {
                if (!allVisited.contains(node)) {
                    return false; 
                }
            }
            if(!outNodes.contains(node) && !startNodes.contains(node)) {
                if (!allVisited.contains(node) || graph.get(node).isEmpty()) {
                    return false; 
                }
            }
            
        }
        
        return true;
    }
    
    private void dfsTraversal(int node, Set<Integer> visited) {
        visited.add(node);
        
        // Traverse neighbors
        if (graph.containsKey(node)) {
            for (int neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    dfsTraversal(neighbor, visited);
                }
            }
        }
    }

    public void removeConnection(int in, int out) {

        if(graph.containsKey(in)) {

            if(graph.get(in).contains(out)) {
                graph.get(in).remove((Integer)out);
                return;
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

        graph.addConnection(1, 4);
        graph.addConnection(1, 5);
        graph.addConnection(1, 6);
        graph.addConnection(2, 4);
        graph.addConnection(2, 5);
        graph.addConnection(2, 6);
        graph.addConnection(3, 4);
        graph.addConnection(3, 5);
        graph.addConnection(3, 6);

        Set<Integer> startNodes = new HashSet<>();
        Set<Integer> outNodes = new HashSet<>();

        for(int i = 1; i <= 3; i++) {
            startNodes.add(i);
        }

        for(int i = 3 + 1; i <= 3 + 3; i++) {
            outNodes.add(i);
        }
        
        // graph.removeConnection(1, 4);
        // graph.removeConnection(1, 5);
        // graph.removeConnection(1, 6);

        System.out.println("Has Loop: " + graph.hasLoop()); // Output: true
        System.out.println("Is Connected Graph: " + graph.hasNoIsolatedNodes(startNodes, outNodes)); // Output: false
    }
}