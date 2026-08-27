package turtle;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Headless turtle state machine mirroring Python's {@code turtle} module; no GUI dependency. */
public class Turtle {
    private Vector2D position;
    private double heading;
    private int speed;
    private Pen pen;
    private Color fillColor;
    private boolean isFilling;
    private List<Vector2D> fillPath;
    private List<LineSegment> segments;
    private List<FilledPolygon> filledPolygons;
    private List<Movement> movements;

    /**
     * Creates a turtle at {@code (0, 0)}, heading {@code 0} degrees (east),
     * with the pen down, black pen and fill colors, and width {@code 1.0}.
     */
    public Turtle() {
        position = new Vector2D(0, 0);
        heading = 0;
        speed = 3;
        pen = new Pen();
        fillColor = Color.BLACK;
        isFilling = false;
        segments = new ArrayList<>();
        filledPolygons = new ArrayList<>();
        fillPath = new ArrayList<>();
        movements = new ArrayList<>();
    }

    /**
     * Moves forward by {@code distance} units in the current heading.
     * Records a segment when the pen is down and a fill vertex when filling.
     * A distance of zero is a no-op.
     *
     * @param distance signed movement distance
     */
    public void forward(double distance) {
        if (distance == 0) return;
        double radians = degreesToRadians(heading);
        Vector2D newDestination = position.add(
            new Vector2D(distance * Math.cos(radians), distance * Math.sin(radians))
        );
        goTo(newDestination);
    }

    /**
     * Moves backward by {@code distance} units without changing heading.
     *
     * @param distance movement distance
     */
    public void backward(double distance) {
        forward(-distance);
    }

    /**
     * Turns counter-clockwise by {@code angle} degrees and normalizes heading
     * to {@code [0, 360)}.
     *
     * @param angle turn angle in degrees
     */
    public void left(double angle) {
        heading += angle;
        heading = ((heading % 360) + 360) % 360;
    }

    /**
     * Turns clockwise by {@code angle} degrees and normalizes heading to
     * {@code [0, 360)}.
     *
     * @param angle turn angle in degrees
     */
    public void right(double angle) {
        left(-angle);
    }

    /**
     * Changes the turtle speed to {@code level} with the following rules:
     *  0 means instant/no animation.
     *  1–10 represent slow-to-fast animation speeds.
     *  Values below 0.5 or at/above 10.5 become 0.
     *  Other numeric values are rounded to the nearest integer.
     * 
     * Changing speed does not itself move or animate the turtle.
     * 
     * @param level affects animation timing speed, not the geometric result of movement.
     */
    public void speed(double level) {
        // Numeric values below 0.5 or at or above 10.5 are converted to 0.
        if (level < 0.5 || level >= 10.5) {
            level = 0;
        }
        // Values from 0.5 up to, but not including, 10.5 are rounded and stored as an integer.
        speed = (int) (level + 0.5);
    }

    /**
     * Moves to absolute turtle coordinates and records a segment if the pen
     * is down. Does not change heading.
     *
     * @param x destination x-coordinate
     * @param y destination y-coordinate
     */
    public void goTo(double x, double y) {
        goTo(new Vector2D(x, y));
    }

    /**
     * Moves to the origin, recording a segment if the pen is down.
     * Does not reset heading or clear history.
     */
    public void home() {
        goTo(0,0);
    }

    /** Returns the color used for future filled polygons. */
    public Color getFillColor() {
        return new Color(fillColor.getRGB());
    }

    /** 
     * Returns the current speed setting.
     *
     * @return the current speed from 0 through 10
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Returns completed polygons in creation order.
     *
     * @return an unmodifiable view of completed filled polygons
     */
    public List<FilledPolygon> getFilledPolygons() {
        return Collections.unmodifiableList(filledPolygons);
    }

    /**
     * Returns whether a fill path is currently being recorded.
     *
     * @return {@code true} between {@link #beginFill()} and {@link #endFill()}
     */
    public boolean isFilling() {
        return isFilling;
    }

    /**
     * Starts recording a fill path at the turtle's current position.
     * This method does not move the turtle or create a line segment.
     *
     * @throws IllegalStateException if a fill is already in progress
     */
    public void beginFill() {
        // Calling beginFill() while already filling has explicit behavior, preferably IllegalStateException.
        if (isFilling) {
            throw new IllegalStateException("Turtle cannot begin filling when it is already filling a shape.");
        }
        // beginFill() marks the turtle as actively recording a fill.
        isFilling = true;
        fillPath = new ArrayList<>();

        // The current position is the first point in the in-progress path.
        fillPath.add(
            new Vector2D(
                getPosition().getX(),
                getPosition().getY()
            )
        );
    }

    /**
     * Completes the active fill path and publishes it as a filled polygon.
     * The polygon is created only when the path contains at least three points.
     * Calling this method without an active fill is a no-op.
     *
     * <p>This method ends the filling state without changing the turtle's
     * position, heading, pen state, or pen color.</p>
     */
    public void endFill() {
        // Does nothing if no fill is currently active.
        if (!isFilling) {
            fillPath.clear();
            return;
        }

        // Fewer than three points produce no polygon and still end the fill.
        if (fillPath.size() < 3) {
            isFilling = false;
            fillPath.clear();
            return;
        }

        filledPolygons.add(new FilledPolygon(fillPath, fillColor, movements.size() - 1));
        fillPath.clear();
        isFilling = false;
    }

    /**
     * Sets the color used for future filled polygons.
     *
     * @param theColor color to use for future fills
     * @throws NullPointerException if {@code theColor} is null
     */
    public void fillColor(Color theColor) {
        fillColor = Objects.requireNonNull(theColor);
    }

    /**
     * Returns the turtle's current turtle-space position.
     *
     * @return current position
     */
    public Vector2D getPosition() {
        return position;
    }

    /**
     * Returns the turtle's current heading in degrees.
     *
     * @return current heading; {@link #left(double)} and {@link #right(double)}
     *         keep it in {@code [0, 360)}, while {@link #setHeading(double)}
     *         preserves the supplied value
     */
    public double getHeading() {
        return heading;
    }

    /**
     * Returns the turtle's mutable pen state.
     *
     * @return the live pen object
     */
    public Pen getPen() {
        return pen;
    }

    /** Lifts the pen so subsequent movement does not record segments. */
    public void penUp() { pen.penUp(); }

    /** Lowers the pen so subsequent movement records segments. */
    public void penDown() { pen.penDown(); }

    /**
     * Returns all segments recorded since construction in creation order.
     *
     * @return an unmodifiable view of the segment history
     */
    public List<LineSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    /**
     * Changes the turtle's current pen color.
     * The color applies to subsequently drawn segments; previously recorded
     * segments retain their original colors.
    *
    * @param theColor color to use for subsequent segments
     * @throws NullPointerException if {@code theColor} is null
     */
    public void penColor(Color theColor) {
        pen.setColor(
            Objects.requireNonNull(theColor)
        );
    }

    /**
     * Sets the width used for subsequently drawn segments.
     * Previously recorded segments retain their original widths.
     *
     * @param theWidth positive stroke width
     * @throws IllegalArgumentException if {@code theWidth} is zero or negative
    */
   public void penWidth(double theWidth) {
       pen.setWidth(theWidth);
    }

    /**
     * Sets heading directly in degrees without normalizing it.
     *
     * @param angle heading in degrees
     */
    public void setHeading(double angle) {
        heading = angle;
    }

    private void goTo(Vector2D destination) {
        movements.add(new Movement(position, destination, pen.isDown(), heading));

        if (pen.isDown()) {
            segments.add(
                new LineSegment(
                    position,
                    destination,
                    pen.getColor(),
                    pen.getWidth()
                )
            );
        }

        if (isFilling) {
            fillPath.add(destination);
        }

        position = destination;
    }

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    List<Vector2D> getFillPath() {
        return new ArrayList<>(fillPath);
    }

    Vector2D getstartFillPosition() {
        return new Vector2D(fillPath.get(0));
    }

    List<Movement> getMovements() {
        return movements;
    }
}