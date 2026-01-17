import java.awt.Point;
import java.util.Random;

public class Location {
    public String name;
    private double difficulty_multiplier;
    public int centerX, centerY;
    public int radius = 100;

    public Location(String name, double diff, int x, int y) {
        this.name = name;
        this.difficulty_multiplier = diff;
        this.centerX = x;
        this.centerY = y;
    }

    // Generates a random coordinate within this location's circle
    public Point getRandomPointInside() {
        Random rng = new Random();
        double angle = rng.nextDouble() * 2 * Math.PI;
        // Use square root for uniform distribution across the circle area
        double r = radius * Math.sqrt(rng.nextDouble());

        int x = (int) (centerX + r * Math.cos(angle));
        int y = (int) (centerY + r * Math.sin(angle));
        return new Point(x, y);
    }

    public double getDiff() {
        return difficulty_multiplier;
    }
}