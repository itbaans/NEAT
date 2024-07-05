package TheGame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.awt.Color;
import java.awt.Graphics;

public class MapGenerator {

    int height;
    int width;
    int iterations; 
    int birthRate;
    int tileSize = 10;
    float density;
    Point start;
    Point goal;
    int goalArea;

    boolean[][] theMap;
    Tile[][] tiles;

    public MapGenerator() {

        height = GameConstants.height;
        width = GameConstants.width;
        iterations = GameConstants.iterations;
        birthRate = GameConstants.birthRate;
        density = GameConstants.density;
        goalArea = GameConstants.goalArea;

        start = getRandomStartingPoint();
        goal = getRandomGoalPoint();

        // System.out.println("Start Point: " + start);
        // System.out.println("Goal Point: " + goal);


        theMap = new boolean[height][width];
        tiles = new Tile[height][width];

        generateMap();
    }

    public Point getRandomStartingPoint() {

        int yMin = (int)(height * 0.7);
        int yMax = (int)(height * 0.8);
        int y = (int)(Math.random() * (yMax - yMin + 1) + yMin);

        int xMin = (int)(width * 0.3);
        int xMax = (int)(width * 0.8);
        int x = (int)(Math.random() * (xMax - xMin + 1) + xMin);

        return new Point(x, y);

    }

    public Point getRandomGoalPoint() {

        int max = (int)(height * 0.3);
        int yMin = (int)(height * 0.1);
        int y = (int)(Math.random() * (max - yMin + 1) + yMin);

        int xMin = (int)(width * 0.3);
        int xMax = (int)(width * 0.8);
        int x = (int)(Math.random() * (xMax - xMin + 1) + xMin);

        return new Point(x, y);

    }

    public void generateMap() {

        start = getRandomStartingPoint();
        goal = getRandomGoalPoint();
        
        generateNoiseGrid();
        automata();

        while(!aStarSearch()) {
            generateNoiseGrid();
            automata();
        }

        tiles = getMapArray();

    }
    
    public void printGrid() {
        
        for(int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {

                if(theMap[i][j]) System.out.print(1);
                else System.out.print(0);

            }

            System.out.println();

        }

    }

    public void resetVisited() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tiles[i][j].isVisited = false;
            }
        }
    }

    private void automata() {

        for(int i = 0; i < iterations; i++) {

            boolean[][] tempGrid = new boolean[height][width];

            for (int j = 0; j < height; j++) {

                for (int k = 0; k < width; k++) {

                    //true means floor
                    if(j < height - 1 && k < width - 1 && j > 0 && k > 0) //tempGrid[j][k] = true;
                        if(countNeighbours(j, k, theMap) <= birthRate || startCheck(j, k) || goalCheck(j, k)) tempGrid[j][k] = true;
                    else tempGrid[j][k] = false;
                            
                }
            }
           
            theMap = tempGrid;
        }

    }

    private boolean startCheck(int y, int x) {
        return (y >= start.y && y < start.y + goalArea && x >= start.x && x < start.x + goalArea);
    }

    private boolean goalCheck(int y, int x) {
        return (y >= goal.y && y < goal.y + goalArea && x >= goal.x && x < goal.x + goalArea);
    }

    private int countNeighbours(int r, int c, boolean[][] grid) {

        int count = 0;

        for(int i = r - 1; i <= r + 1; i++) {

            for (int j = c - 1; j <= c + 1; j++) {

                if(i >= 0 && j >= 0 && i < height && j < width) {

                    if(!grid[i][j]) count++;

                }

                else count++;
                
            }

        }

        if(!grid[r][c]) return count - 1;

        return count;

    }

    private void generateNoiseGrid() {

        double rand = Math.random();

        for(int i = 0; i < height; i++) {

            for (int j = 0; j < width; j++) {

                if(rand > density) theMap[i][j] = true;
                else theMap[i][j] = false;

                rand = Math.random();

            }

        }
    }

    public void draw(Graphics g) {

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                tile.draw(g);
            }
        }

        g.setColor(Color.red);
        g.fillRect(start.x * tileSize, start.y * tileSize, tileSize * goalArea, tileSize * goalArea);

        // g.setColor(Color.red);
        // g.drawRect(0 * tileSize, 0 * tileSize, tileSize * 2, tileSize * 2);

        g.setColor(Color.green);
        g.fillRect(goal.x * tileSize, goal.y * tileSize, tileSize * goalArea, tileSize * goalArea);

    }

    public Tile[][] getMapArray() {

        Tile[][] temp = new Tile[height][width];

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if(theMap[i][j]) temp[i][j] = new Tile(tileSize, new Point(j * tileSize, i * tileSize), Color.white, false);
                else temp[i][j] = new Tile(tileSize, new Point(j * tileSize, i * tileSize), Color.black, true);
            }
        }
        return temp;
    }

    // A* search method
    public boolean aStarSearch() {
        PriorityQueue<AStarNode> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
        HashSet<AStarNode> closedList = new HashSet<>();

        AStarNode startNode = new AStarNode(start, null, 0, heuristic(start, goal));
        openList.add(startNode);

        while (!openList.isEmpty()) {
            AStarNode current = openList.poll();
            if (current.position.equals(goal)) {
                // Path found
                return true;
            }

            closedList.add(current);
            for (AStarNode neighbor : getNeighbors(current)) {
                if (closedList.contains(neighbor)) {
                    continue;
                }
                if (!openList.contains(neighbor) || neighbor.g < current.g + 1) {
                    neighbor.parent = current;
                    neighbor.g = current.g + 1;
                    neighbor.f = neighbor.g + neighbor.h;
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }
        // No path found
        return false;
    }

    // Heuristic function
    private int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    // Get neighbors
    private List<AStarNode> getNeighbors(AStarNode node) {
        List<AStarNode> neighbors = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        for (int[] direction : directions) {
            int newX = node.position.x + direction[0];
            int newY = node.position.y + direction[1];
            if (newX >= 0 && newY >= 0 && newX < width && newY < height && theMap[newY][newX]) {
                neighbors.add(new AStarNode(new Point(newX, newY), node, node.g + 1, heuristic(new Point(newX, newY), goal)));
            }
        }
        return neighbors;
    }

    // A* node class
    class AStarNode {

        Point position;
        AStarNode parent;
        int g; // Cost from start
        int h; // Heuristic cost to goal
        int f; // Total cost

        public AStarNode(Point position, AStarNode parent, int g, int h) {
            this.position = position;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AStarNode aStarNode = (AStarNode) o;
            return position.equals(aStarNode.position);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position);
        }
    }

}
