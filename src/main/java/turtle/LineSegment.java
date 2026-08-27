package turtle;

import java.awt.Color;
import java.util.Objects;

/**
 * Immutable record of a drawn stroke in turtle-space coordinates.
 * Captures pen color and width at the time of drawing.
 */
public class LineSegment {
    private final Vector2D from;
    private final Vector2D to;
    private final Color color;
    private final double width;

    /**
    * @param initFrom segment starting point
    * @param initTo segment ending point
    * @param initColor stroke color
    * @param initWidth positive stroke width
    * @throws NullPointerException if {@code initFrom}, {@code initTo}, or
    *         {@code initColor} is null
     * @throws IllegalArgumentException if width is zero or negative
     */
    public LineSegment(
        Vector2D initFrom,
        Vector2D initTo,
        Color initColor,
        double initWidth
    ) {
        Objects.requireNonNull(initFrom, "Vector2D from cannot be null");
        Objects.requireNonNull(initTo, "Vector2D to cannot be null");
        Objects.requireNonNull(initColor, "Color cannot be null");

        if(initWidth <= 0) {
            throw new IllegalArgumentException("Line Segment width must be positive: " + initWidth);
        }

        from = initFrom;
        to = initTo;
        color = initColor;
        width = initWidth;
    }

    public LineSegment(LineSegment lineSegment) {
        this(
            lineSegment.from, 
            lineSegment.to, 
            lineSegment.color, 
            lineSegment.width
        );
    }

    /** Returns the segment's starting point. */
    public Vector2D getFrom() {
        return from;
    }

    /** Returns the segment's ending point. */
    public Vector2D getTo() {
        return to;
    }

    /** Returns the color captured when the segment was recorded. */
    public Color getColor() {
        return color;
    }

    /** Returns the stroke width captured when the segment was recorded. */
    public double getWidth() {
        return width;
    }

    @Override
    public String toString() {
        return "LineSegment [from=" + from + ", to=" + to + ", color=" + color + ", width=" + width + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        } else if (this == other) {
            return true;
        }

        LineSegment obj = (LineSegment) other;
        return this.from.equals(obj.getFrom())
            && this.to.equals(obj.getTo())
            && this.width == obj.getWidth()
            && this.color.equals(obj.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, width, color);
    }
}
