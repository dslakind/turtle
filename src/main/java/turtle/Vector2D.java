package turtle;

import java.util.Objects;

/**
 * Immutable two-dimensional vector represented by Cartesian coordinates.
 *
 * All operations return new {@code Vector2D} instances and do not modify
 * the current object.
 */
public class Vector2D {
    private final double x;
    private final double y;

    /**
     * Creates a vector with the supplied Cartesian coordinates.
     *
     * @param initX x-coordinate
     * @param initY y-coordinate
     */
    public Vector2D(double initX, double initY) {
        x = initX;
        y = initY;
    }

    /**
     * Creates an independent copy of another vector.
     *
     * @param position vector whose coordinates are copied
     * @throws NullPointerException if {@code position} is null
     */
    public Vector2D(Vector2D position) {
        this(position.x, position.y);
    }

    /**
     * Returns the coordinate-wise sum of this vector and {@code other}.
     *
     * @param other vector to add
     * @return a new vector containing the coordinate-wise sum
     * @throws NullPointerException if {@code other} is null
     */
    public Vector2D add(Vector2D other) {
        double resultX = this.x + other.x;
        double resultY = this.y + other.y;

        return new Vector2D(resultX, resultY);
    }

    /**
     * Returns the Euclidean distance between this vector's coordinates
     * and those of {@code other}.
     *
     * @param other vector whose coordinates are used for the distance
     *              calculation
     * @return the Euclidean distance between the two coordinate pairs
     * @throws NullPointerException if {@code other} is null
     */
    public double distanceTo(Vector2D other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns the x-coordinate.
     *
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate.
     *
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        if (this == other) {
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