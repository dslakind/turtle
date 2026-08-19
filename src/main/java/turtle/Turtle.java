package turtle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Headless turtle state machine mirroring Python's {@code turtle} module; no GUI dependency. */
public class Turtle {
    private Vector2D position;
    private double heading;
    private Pen pen;
    private List<LineSegment> segments;

    /** Starts at origin (0,0), heading 0° (east), pen down, black, width 1. */
    public Turtle() {
        position = new Vector2D(0, 0);
        heading = 0;
        pen = new Pen();
        segments = new ArrayList<>();
    }

    /** Moves forward {@code distance} units; records a segment if the pen is down. No-op when distance is 0. */
    public void forward(double distance) {
        if (distance == 0) return;
        double radians = degreesToRadians(heading);
        Vector2D newDestination = position.add(
            new Vector2D(distance * Math.cos(radians), distance * Math.sin(radians))
        );
        goTo(newDestination);
    }

    /** Equivalent to {@code forward(-distance)}. */
    public void backward(double distance) {
        forward(-distance);
    }

    /** Turns counter-clockwise by {@code angle} degrees; heading is normalised to [0, 360). */
    public void left(double angle) {
        heading += angle;
        heading = ((heading % 360) + 360) % 360;
    }

    /** Turns clockwise by {@code angle} degrees; heading is normalised to [0, 360). */
    public void right(double angle) {
        left(-angle);
    }

    /** Teleports to (x, y) and records a segment if the pen is down. Does not change heading. */
    public void goTo(double x, double y) {
        goTo(new Vector2D(x, y));
    }

    private void goTo(Vector2D destination) {
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
        position = destination;
    }

    /** Returns to origin; records a segment if the pen is down. Does not reset heading. */
    public void home() {
        goTo(0,0);
    }

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public Vector2D getPosition() {
        return position;
    }

    public double getHeading() {
        return heading;
    }

    public Pen getPen() {
        return pen;
    }

    public void penUp() { pen.penUp(); }

    public void penDown() { pen.penDown(); }

    /** Returns an unmodifiable view of all segments recorded since construction. */
    public List<LineSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    /** Sets heading directly in degrees. Does not normalise to [0, 360) unlike {@code left}/{@code right}. */
    public void setHeading(double angle) {
        heading = angle;
    }
}