package turtle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable polygon captured by a completed turtle fill operation.
 * Points remain in turtle-space order; the renderer performs coordinate mapping.
 */
public class FilledPolygon {
    private final List<Vector2D> points;
    private final Color color;
    private final int completionMovementIndex; // the index of the last Movement that belongs to this fill.

    /**
     * Creates a polygon from ordered turtle-space points and a fill color.
     * The supplied point list is copied so later changes do not affect this polygon.
     *
     * @param initPoints ordered polygon vertices
     * @param initColor fill color
     * @throws NullPointerException if the point list, a point, or the color is null
     */
    public FilledPolygon(List<Vector2D> initPoints, Color initColor, int initCompletionMovementIndex) {
        completionMovementIndex = initCompletionMovementIndex;
        points = new ArrayList<>();
        // validate non-null points
        for (Vector2D point : initPoints) {
            points.add(
                Objects.requireNonNull(
                    point,
                    "The points in FilledPolygon cannot be null")
            );
        }
        color = Objects.requireNonNull(initColor, "FilledPolygon cannot have a null Color");
    }

    /**
     * Returns defensive copies of the polygon vertices in their original order.
     *
     * @return an unmodifiable list of polygon vertices
     */
    public List<Vector2D> getPoints() {
        ArrayList<Vector2D> copyPointsList = new ArrayList<>();
        for (Vector2D point : points) {
            copyPointsList.add(
                new Vector2D(
                    Objects.requireNonNull(point, "The points in FilledPolygon cannot be null")
                )
            );
        }
        return Collections.unmodifiableList(copyPointsList);
    }

    /**
     * Returns the color captured when the polygon was completed by
     * {@code Turtle.endFill()}.
     *
     * @return the polygon fill color
     */
    public Color getFillColor() {
        return color;
    }

    /**
     * Returns the index of the last movement that belongs to this polygon.
     *
     * The completion movement index is used by the renderer to determine
     * when the filled polygon should become visible during animation.
     *
     * @return the index of the movement that completes this polygon
     */
    public int getCompletionMovementIndex() {
        return completionMovementIndex;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((points == null) ? 0 : points.hashCode());
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FilledPolygon other = (FilledPolygon) obj;
        if (points == null) {
            if (other.points != null)
                return false;
        } else if (!points.equals(other.points))
            return false;
        if (color == null) {
            if (other.color != null)
                return false;
        } else if (!color.equals(other.color))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "FilledPolygon [points=" + points + ", color=" + color + "]";
    }
}