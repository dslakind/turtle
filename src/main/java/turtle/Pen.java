package turtle;

import java.awt.Color;
import java.util.Objects;

/** Mutable pen state: whether it draws, its color, and its stroke width. */
public class Pen {
    private boolean isDown;
    private Color color;
    private double width;

    /**
     * Creates a pen with the supplied state, color, and stroke width.
     *
    * @param isDown whether movement draws segments
    * @param initColor current stroke color
    * @param initWidth positive stroke width
     * @throws IllegalArgumentException if {@code initWidth} is zero or negative
     */
    public Pen(boolean isDown, Color initColor, double initWidth) {
        this.isDown = isDown;
        color = initColor;
        setWidth(initWidth);
    }

    /** Defaults: pen down, black, width 1.0 — matches Python turtle defaults. */
    public Pen() {
        this(true, Color.BLACK, 1);
    }

    /**
     * Creates an independent copy of another pen's state.
     *
     * @param pen pen whose state is copied
     * @throws NullPointerException if {@code pen} is null
     */
    public Pen(Pen pen) {
        this(pen.isDown, pen.color, pen.width);
    }

    /** Lifts the pen so movement is not drawn. */
    public void penUp() {
        this.isDown = false;
    }

    /** Lowers the pen so movement is drawn. */
    public void penDown() {
        this.isDown = true;
    }

    /** Returns whether the pen currently draws movement. */
    public boolean isDown() {
        return isDown;
    }

    /** Returns the current pen color. */
    public Color getColor() {
        return color;
    }

    /** Returns the current stroke width. */
    public double getWidth() {
        return width;
    }

    /** Sets the color used for subsequently recorded segments. */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Sets the stroke width for subsequently recorded segments.
     *
     * @param width positive stroke width
     * @throws IllegalArgumentException if {@code width} is zero or negative
     */
    public void setWidth(double width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive:" + width);
        }

        this.width = width;
    }

    @Override
    public String toString() {
        return "Pen [isDown=" + isDown + ", color=" + color + ", width=" + width + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        } else if (this == other) {
            return true;
        }

        Pen pen = (Pen) other;
        return this.isDown == pen.isDown
            && this.color.equals(pen.color)
            && this.width == pen.width;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDown, color, width);
    }
}
