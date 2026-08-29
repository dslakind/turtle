package turtle;

import java.util.Objects;

/**
 * Represents one recorded movement of a {@link Turtle}.
 *
 * A movement stores the turtle's starting position, ending position,
 * pen state, and heading at the time the movement occurred.
 *
 * Unlike {@link LineSegment}, a Movement is recorded whether the pen is
 * down or up. This allows the animation system to reproduce the turtle's
 * visible motion even when no line is being drawn.
 *
 * Movement objects are immutable. Position values are defensively copied
 * when stored and when returned, so later changes cannot alter the recorded
 * historical movement.
 */
public class Movement {

    private final Vector2D from;
    private final Vector2D to;
    private final boolean penDown;
    private final double heading;

    /**
     * Creates a record of one turtle movement.
     *
     * The starting and ending positions are copied so that the Movement
     * preserves the coordinates that existed when it was created.
     *
     * @param initFrom starting position of the movement
     * @param initTo ending position of the movement
     * @param initPenDown whether the pen was down during the movement
     * @param initHeading turtle heading, in degrees, during the movement
     * @throws NullPointerException if {@code initFrom} or {@code initTo} is null
     * @throws IllegalArgumentException if {@code initHeading} is not finite
     */
    public Movement(
        Vector2D initFrom,
        Vector2D initTo,
        boolean initPenDown,
        double initHeading
    ) {
        from = new Vector2D(Objects.requireNonNull(initFrom, "Movement start cannot be null"));
        to = new Vector2D(Objects.requireNonNull(initTo, "Movement end cannot be null"));
        if (!Double.isFinite(initHeading)) {
            throw new IllegalArgumentException("Movement heading must be finite: " + initHeading);
        }
        penDown = initPenDown;
        heading = initHeading;
    }

    /**
     * Returns the starting position of this movement.
     *
     * @return the position from which the turtle moved
     */
    public Vector2D getFrom() {
        return new Vector2D(from);
    }

    /**
     * Returns the ending position of this movement.
     *
     * @return the position to which the turtle moved
     */
    public Vector2D getTo() {
        return new Vector2D(to);
    }

    /**
     * Returns whether the turtle's pen was down during this movement.
     *
     * A pen-down movement may produce a visible line, while a pen-up
     * movement changes the turtle's visible position without drawing.
     *
     * @return true if the pen was down during the movement; false otherwise
     */
    public boolean isPenDown() {
        return penDown;
    }

    /**
     * Returns the turtle's heading during this movement.
     *
     * The heading is stored so the renderer can display the turtle cursor
     * pointing in the historical direction of travel while animations are
     * replayed.
     *
     * @return the heading, in degrees, recorded for this movement
     */
    public double getHeading() {
        return heading;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Movement movement = (Movement) other;
        return penDown == movement.penDown
            && Double.compare(heading, movement.heading) == 0
            && from.equals(movement.from)
            && to.equals(movement.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, penDown, heading);
    }
}