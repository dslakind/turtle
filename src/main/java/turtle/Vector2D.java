package turtle;

import java.util.Objects;

public class Vector2D {
    private final double x;
    private final double y;

    public Vector2D(double initX, double initY) {
        x = initX;
        y = initY;
    }

    public Vector2D add(Vector2D other) {
        double resultX = this.x + other.x;
        double resultY = this.y + other.y;

        return new Vector2D(resultX, resultY);
    }

    public double distanceTo(Vector2D other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        if(this == other) {
            return true;
        } 
        Vector2D obj = (Vector2D) other;
        return this.x == obj.x 
                && this.y == obj.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Vector2D [x=" + x + ", y=" + y + "]";
    }
    
    
}
