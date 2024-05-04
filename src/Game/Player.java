package Game;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.util.Map;


import org.apache.commons.geometry.euclidean.twod.Vector2D;

import NeuralNetwork.NueralNetwork;

public class Player implements MadeForNeat {
    
    Vector2D pos;
    Vector2D velocity;
    Vector2D accelaration;
    double visionRange = 50;
    double visionAngle = Math.PI;
    int size;
    double maxSpeed;
    double maxForce;
    Color visionCol = new Color(255, 0, 0);
    Vector2D[] tracers = new Vector2D[10];
    double[][] whatTracersSee = new double[2][10];
    float angleThreshold = 0.01f;
    NueralNetwork brain;
    

    public Player(int x, int y) {

        pos = Vector2D.of(x, y);
        velocity = Vector2D.of(0.5, 0.5);
        accelaration = Vector2D.of(0, 0);
        size = 6;
        maxForce = 0.2;
        maxSpeed = 8;

    }

    public void update() {

        velocity = velocity.add(accelaration);
        velocity = limitVector(velocity, maxSpeed);
        pos = pos.add(velocity);
        updateTracers();
        accelaration = accelaration.multiply(0);
        
    }


    public boolean inVision(int x, int y) {

        Vector2D toTarget = Vector2D.of(x, y).subtract(pos);
        double angle = Math.acos(dotProduct(toTarget, velocity) / (toTarget.norm() * velocity.norm()));
    
        // Ensure the angle is within the range [-pi, pi]
        double crossProduct = toTarget.getX() * velocity.getY() - toTarget.getY() * velocity.getX();
        if (crossProduct < 0) {
            angle = -angle; // Adjust angle if the cross product is negative
        }
    
        return Math.abs(angle) <= visionAngle / 2 && (getVectorLength(toTarget) <= visionRange);
    }

    public boolean touchesTile(Vector2D tracerStart, Vector2D tracerEnd, int tileX, int tileY, int tileSize) {
        // Define the boundaries of the square tile
        int tileLeft = tileX;
        int tileRight = tileX + tileSize;
        int tileTop = tileY;
        int tileBottom = tileY + tileSize;
    
        // Check for intersection with each edge of the square tile
        boolean intersectsLeftEdge = tracerIntersectsEdge(tracerStart, tracerEnd, tileLeft, tileTop, tileLeft, tileBottom);
        boolean intersectsRightEdge = tracerIntersectsEdge(tracerStart, tracerEnd, tileRight, tileTop, tileRight, tileBottom);
        boolean intersectsTopEdge = tracerIntersectsEdge(tracerStart, tracerEnd, tileLeft, tileTop, tileRight, tileTop);
        boolean intersectsBottomEdge = tracerIntersectsEdge(tracerStart, tracerEnd, tileLeft, tileBottom, tileRight, tileBottom);
    
        // Check if any intersection point lies within the bounds of the tile
        return (intersectsLeftEdge || intersectsRightEdge || intersectsTopEdge || intersectsBottomEdge);
    }
    
    // Helper method to check if the tracer vector intersects with an edge defined by two points
    private boolean tracerIntersectsEdge(Vector2D tracerStart, Vector2D tracerEnd, int edgeX1, int edgeY1, int edgeX2, int edgeY2) {
        Vector2D edgeStart = Vector2D.of(edgeX1, edgeY1);
        Vector2D edgeEnd = Vector2D.of(edgeX2, edgeY2);
        return tracerIntersectsSegment(tracerStart, tracerEnd, edgeStart, edgeEnd);
    }
    
    // Helper method to check if two line segments intersect
    private boolean tracerIntersectsSegment(Vector2D tracerStart, Vector2D tracerEnd, Vector2D segmentStart, Vector2D segmentEnd) {
        // Implementation of line segment intersection algorithm (e.g., using cross product)
        // You can find various algorithms online or use third-party libraries
        // Here, we assume you have an implementation named 'lineIntersectsSegment' that checks for intersection
        return lineIntersectsSegment(tracerStart, tracerEnd, segmentStart, segmentEnd);
    }

    private boolean lineIntersectsSegment(Vector2D p1, Vector2D p2, Vector2D p3, Vector2D p4) {
        double o1 = orientation(p1, p2, p3);
        double o2 = orientation(p1, p2, p4);
        double o3 = orientation(p3, p4, p1);
        double o4 = orientation(p3, p4, p2);
    
        // General case (segments intersect)
        if (o1 != o2 && o3 != o4) return true;
    
        // Special cases (collinear points)
        if (o1 == 0 && onSegment(p1, p3, p2)) return true;
        if (o2 == 0 && onSegment(p1, p4, p2)) return true;
        if (o3 == 0 && onSegment(p3, p1, p4)) return true;
        if (o4 == 0 && onSegment(p3, p2, p4)) return true;

        return false; // No intersection
    }

    // Helper method to calculate orientation of triplet (p1, p2, p3)
    private double orientation(Vector2D p1, Vector2D p2, Vector2D p3) {
        double val = (p2.getY() - p1.getY()) * (p3.getX() - p2.getX()) -
                    (p2.getX() - p1.getX()) * (p3.getY() - p2.getY());
        if (val == 0) return 0; // Collinear
        return (val > 0) ? 1 : 2; // Clockwise or counterclockwise
    }

    // Helper method to check if point q lies on line segment pr
    private boolean onSegment(Vector2D p, Vector2D q, Vector2D r) {
        return q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX()) &&
            q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY());
    }

    public boolean inVisionOfTracer(Vector2D tracer, int x, int y) {
        Vector2D target = Vector2D.of(x, y);
        
        // Compute the distance between the tracer and the target point
        double distance = pos.distance(target);
        
        // Compute the angle between the tracer direction and the direction towards the target
        double angle = Math.acos(dotProduct(target, tracer) / (target.norm() * tracer.norm()));
        
        // Check if the absolute value of the angle is close to 0 (or π) and the distance is within the vision range
        return angle <= angleThreshold && distance <= visionRange;
    }

    public double dotProduct(Vector2D v1, Vector2D v2) {
        return v1.getX() * v2.getX() + v1.getY() * v2.getY();
    }

    public void applyForce(Vector2D p) {
        accelaration = accelaration.add(p);
    }

    public void steerLeft() {
        double currentAngle = Math.atan2(velocity.getY(), velocity.getX());
        double newAngle = currentAngle + 0.08;
    
        Vector2D newVelocity = Vector2D.of(Math.cos(newAngle), Math.sin(newAngle)).multiply(velocity.norm());
        velocity = newVelocity;
        //applyForce(newVelocity.subtract(velocity));
    }
    
    public void steerRight() {
        double currentAngle = Math.atan2(velocity.getY(), velocity.getX());
        double newAngle = currentAngle - 0.08;
    
        Vector2D newVelocity = Vector2D.of(Math.cos(newAngle), Math.sin(newAngle)).multiply(velocity.norm());
        velocity = newVelocity;
        //applyForce(newVelocity.subtract(velocity));
    }

    public Vector2D limitVector(Vector2D v, double limit) {

        if(getVectorLength(v) > limit) {
            return v.normalize().multiply(limit);
        }

        return v;
    }
    
    public double getVectorLength(Vector2D p) {
        double x = Math.pow(p.getX(), 2);
        double y = Math.pow(p.getY(), 2);
        return Math.pow(x+y, 0.5);
    }

    public void updateTracers() {

        double dirAngle = Math.atan2(velocity.getY(), velocity.getX());
        double startAngle = dirAngle - visionAngle / 2;
        double endAngle = dirAngle + visionAngle / 2;

        for (int i = 0; i < tracers.length; i++) {
        double angle = startAngle + (endAngle - startAngle) * (i / (double) tracers.length);
        double x2 = pos.getX() + visionRange * Math.cos(angle);
        double y2 = pos.getY() + visionRange * Math.sin(angle);
        tracers[i] = Vector2D.of(x2, y2); // Store tracer position directly, not relative to player
    }
    }

    public void drawVisionRange(Graphics2D g) {
    
        g.setColor(visionCol);
    
        // Draw each tracer
        for (int i = 0; i < tracers.length; i++) {
            
            double x1 = pos.getX();
            double y1 = pos.getY();
            double x2 = tracers[i].getX();
            double y2 = tracers[i].getY();
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            //g.drawString(whatTracersSee[0][i]+"", (int) x2 / 3, (int) y2 / 3);
        }
    }

    public void draw(Graphics2D g) {

        double angle = Math.atan2(velocity.getY(), velocity.getX());

        g.setColor(new Color(127, 127, 127)); // fill(127)
        g.setStroke(new BasicStroke(1)); // stroke(0)

        AffineTransform oldTransform = g.getTransform();
        g.translate(pos.getX(), pos.getY());
        g.rotate(angle);

        int[] xPoints = {size * 2, -size * 2, -size * 2};
        int[] yPoints = {0, -size, size};
        g.drawPolygon(xPoints, yPoints, 3); // beginShape(); vertex(); endShape(CLOSE);

        g.setTransform(oldTransform);
    }

    @Override
    public double getFitness() {
        return brain.getFitness();
    }

    @Override
    public void setFitness(double val) {
        brain.setFitness(val);
    }

    
    


}
