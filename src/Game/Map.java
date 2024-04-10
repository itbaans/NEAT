package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Random;

public class Map {
    private int rows;
    private int columns;
    private boolean[][] mapArray;
    Tile[][] tiles;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    int tileSize = 20;
    
    public Map(int rows, int columns, double initialDensity, int startX, int startY, int endX, int endY) {
        this.rows = rows;
        this.columns = columns;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.mapArray = generateMapArray(initialDensity);
        //evolveMap(2, 4, 3);
        //removeDeadEnds();
        this.tiles = getMapArray();
    }


    private boolean[][] generateMapArray(double initialDensity) {
        boolean[][] mapArray = new boolean[rows][columns];
        Random random = new Random();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                mapArray[i][j] = random.nextDouble() > initialDensity;
            }
        }
        return mapArray;
    }

    private void removeDeadEnds() {
        boolean removedDeadEnd;
        do {
            removedDeadEnd = false;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if ((i == startX && j == startY) || (i == endX && j == endY)) {
                        continue; // Skip start and end positions
                    }
                    if (isDeadEnd(i, j)) {
                        mapArray[i][j] = false;
                        removedDeadEnd = true;
                    }
                }
            }
        } while (removedDeadEnd);
    }

    private boolean isDeadEnd(int row, int col) {
        if (!mapArray[row][col]) {
            return false; // Not a wall
        }
        int count = 0;
        // Check horizontal and vertical neighbors
        if (row > 0 && mapArray[row - 1][col]) {
            count++;
        }
        if (row < rows - 1 && mapArray[row + 1][col]) {
            count++;
        }
        if (col > 0 && mapArray[row][col - 1]) {
            count++;
        }
        if (col < columns - 1 && mapArray[row][col + 1]) {
            count++;
        }
        // Check diagonal neighbors
        if (row > 0 && col > 0 && mapArray[row - 1][col - 1]) {
            count++;
        }
        if (row > 0 && col < columns - 1 && mapArray[row - 1][col + 1]) {
            count++;
        }
        if (row < rows - 1 && col > 0 && mapArray[row + 1][col - 1]) {
            count++;
        }
        if (row < rows - 1 && col < columns - 1 && mapArray[row + 1][col + 1]) {
            count++;
        }
        return count <= 1; // Dead end if only one or zero accessible neighboring tiles
    }

    public void evolveMap(int iterations, int birthThreshold, int survivalThreshold) {
        for (int k = 0; k < iterations; k++) {
            mapArray = applyRule(mapArray, birthThreshold, survivalThreshold);
        }
    }

    private boolean[][] applyRule(boolean[][] mapArray, int birthThreshold, int survivalThreshold) {
        boolean[][] newMap = new boolean[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int aliveNeighbors = countAliveNeighbors(mapArray, i, j);
                if (!mapArray[i][j]) {  // If cell is dead
                    if (aliveNeighbors >= birthThreshold) {
                        newMap[i][j] = true;
                    }
                } else {  // If cell is alive
                    if (aliveNeighbors >= survivalThreshold) {
                        newMap[i][j] = true;
                    }
                }
            }
        }
        return newMap;
    }

    private int countAliveNeighbors(boolean[][] mapArray, int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int neighborRow = row + i;
                int neighborCol = col + j;
                // Check boundary conditions
                if (neighborRow >= 0 && neighborRow < rows && neighborCol >= 0 && neighborCol < columns) {
                    if (mapArray[neighborRow][neighborCol]) {
                        count++;
                    }
                }
            }
        }
        // Exclude the current cell itself
        if (mapArray[row][col]) {
            count--;
        }
        return count;
    }

    public void draw(Graphics g) {

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                tile.draw(g);
            }
        }
    }

    public Tile[][] getMapArray() {

        Tile[][] temp = new Tile[rows][columns];

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(mapArray[i][j]) temp[i][j] = new Tile(tileSize, new Point(i * tileSize, j * tileSize), Color.white, false);
                else temp[i][j] = new Tile(50, new Point(i * tileSize, j * tileSize), Color.black, true);
            }
        }
        return temp;
    }
}
