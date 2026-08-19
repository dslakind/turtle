package turtle;

import java.awt.Color;
import java.util.Objects;

public class LineSegment {
    private final Vector2D from;
    private final Vector2D to;
    private final Color color;
    private final double width;

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

    public Vector2D getFrom() {
        return from;
    }

    public Vector2D getTo() {
        return to;
    }

    public Color getColor() {
        return color;
    }

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
