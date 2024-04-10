package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Tile {
    private int size; // Size of the tile (assuming square)
    Point pos; // Position of the top-left corner of the tile
    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    private Color color; // Color of the tile
    private boolean isCollision; // Flag indicating collision
    
    public Tile(int size, Point p, Color color, boolean isCollision) {
        this.size = size;
        pos = new Point(p.x, p.y);
        this.color = color;
        this.isCollision = isCollision;
    }
    
    // Getters and setters
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isCollision() {
        return isCollision;
    }

    public void setCollision(boolean collision) {
        isCollision = collision;
    }
    
    // Draw method using Graphics
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(pos.x, pos.y, size, size);
    }
}