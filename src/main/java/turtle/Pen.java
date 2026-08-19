package turtle;

import java.awt.Color;
import java.util.Objects;

/** Mutable pen state: whether it draws, its color, and its stroke width. */
public class Pen {
    private boolean isDown;
    private Color color;
    private double width;
    
    public Pen(boolean isDown, Color initColor, double initWidth) {
        this.isDown = isDown;
        color = initColor;
        width = initWidth;
    }
    
    /** Defaults: pen down, black, width 1.0 — matches Python turtle defaults. */
    public Pen() {
        this(true, Color.BLACK, 1);
    }

    public void penUp() {
        this.isDown = false;
    }

    public void penDown() {
        this.isDown = true;
    }

    public boolean isDown() {
        return isDown;
    }

    public Color getColor() {
        return color;
    }

    public double getWidth() {
        return width;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    /** @throws IllegalArgumentException if width is zero or negative */
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
