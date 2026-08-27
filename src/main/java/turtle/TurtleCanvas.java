package turtle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import java.util.Objects;

import javax.swing.Timer;
import javax.swing.JPanel;

/** Swing canvas that renders the recorded segments of a live {@link Turtle}. */
public class TurtleCanvas extends JPanel {
    private final Turtle turtle;
    private int visibleMovementIndex;
    private double visibleFractionOfCurrentMovement;
    private final Timer timer;

    /**
    * Creates a canvas for {@code initTurtle} with a white background and a
    * preferred size of 600 by 600 pixels. The actual mapping uses the
    * component's current width and height.
     *
    * @param initTurtle turtle whose recorded segments and completed polygons
    *        are rendered
     * @throws NullPointerException if {@code initTurtle} is null
     */
    public TurtleCanvas(Turtle initTurtle) {
        turtle = Objects.requireNonNull(initTurtle);

        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
        setForeground(turtle.getPen().getColor());
        visibleFractionOfCurrentMovement = 0.0;
        visibleMovementIndex = turtle.getMovements().size();
        timer = new Timer(16, e -> animationTick());
    }

    Turtle getTurtle() {
        return turtle;
    }

    /**
     * Draws the turtle cursor as a small filled isosceles triangle.
     * The triangle is centered on the turtle's visible animation position
     * and points in the turtle's visible heading direction.
     */
    private void drawVisibleTurtle(Graphics2D g2d) {
        // Get the turtle's current visible animation position.
        Vector2D position = getVisibleTurtlePosition();

        // Get the heading associated with the movement currently being animated.
        double heading = getVisibleTurtleHeading();

        // Java's Math.sin() and Math.cos() use radians rather than degrees.
        double radians = Math.toRadians(heading);

        // Calculate these once because every vertex uses the same rotation.
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        /*
        * Define a narrow isosceles triangle in local turtle coordinates.
        *
        * At heading 0 degrees, the triangle points to the right:
        *
        *         rear upper (-6, 4)
        *              *
        *               \
        *                \
        *                 ----> nose (8, 0)
        *                /
        *               /
        *              *
        *         rear lower (-6, -4)
        *
        * The turtle's visible position is at (0, 0) relative to these points.
        */
        double[][] localPoints = {
            { 8,  0},   // nose
            {-6,  4},   // rear upper
            {-6, -4}    // rear lower
        };

        // Build the Swing polygon that will represent the turtle.
        Polygon triangle = new Polygon();

        for (double[] point : localPoints) {
            double localX = point[0];
            double localY = point[1];

            /*
            * Rotate the local point around the turtle's center according
            * to the turtle's heading.
            */
            double rotatedX =
                localX * cos - localY * sin;

            double rotatedY =
                localX * sin + localY * cos;

            /*
            * Translate the rotated point to the turtle's visible position,
            * then convert from turtle coordinates to Swing screen coordinates.
            */
            int screenX = toScreenX(
                position.getX() + rotatedX
            );

            int screenY = toScreenY(
                position.getY() + rotatedY
            );

            // Add this vertex to the triangle.
            triangle.addPoint(screenX, screenY);
        }

        // Draw the turtle cursor as a filled black triangle.
        g2d.setColor(Color.BLACK);
        g2d.fillPolygon(triangle);
    }

    Vector2D getVisibleTurtlePosition() {
        if (visibleFractionOfCurrentMovement >= 1.0 
            || visibleMovementIndex >= turtle.getMovements().size()) {
            return new Vector2D(turtle.getPosition());
        }
        
        Movement movement = turtle.getMovements().get(visibleMovementIndex);
        Vector2D from = movement.getFrom();
        Vector2D to = movement.getTo();
        
        double x = from.getX()
        + (to.getX() - from.getX()) * visibleFractionOfCurrentMovement;

        double y = from.getY()
        + (to.getY() - from.getY()) * visibleFractionOfCurrentMovement;

        return new Vector2D(x, y);
    }

    double getVisibleTurtleHeading() {
        if (visibleMovementIndex >= turtle.getMovements().size()) {
            return turtle.getHeading();        
        }

        return turtle.getMovements().get(visibleMovementIndex).getHeading();
    }

    void advanceAnimationStep() {
        // Check whether there is a segment waiting to be animated.
        if (visibleMovementIndex >= turtle.getMovements().size()) {
            return;
        } 

        // speed 0 requires no incremental animation
        if (turtle.getSpeed() == 0) {
            visibleMovementIndex = turtle.getMovements().size();
            visibleFractionOfCurrentMovement = 0.0;        
            return;    
        }

        // Increase visibleFractionOfCurrentSegment
        visibleFractionOfCurrentMovement += turtle.getSpeed() / 100.0; // placeholder for speed later.

        /* When the fraction reaches 1.0, advance visibleMovementIndex to the 
        next segment and reset the fraction to 0.0. */
        if (visibleFractionOfCurrentMovement >= 1.0 - 0.000000001) {
            visibleMovementIndex++;
            visibleFractionOfCurrentMovement = 0;
        }
    }

    void setAnimationProgress(int movementIndex, double fraction) {
        if (movementIndex < 0 || movementIndex > turtle.getMovements().size()) {
            throw new IllegalArgumentException("movementIndex must be from 0 through turtle.getMovements().size().");
        }

        if (fraction < 0 || fraction > 1 || Double.isNaN(fraction)) {
            throw new IllegalArgumentException("fraction must be between 0.0 and 1.0, inclusive.");
        }

        if (movementIndex == turtle.getMovements().size() && fraction != 0) {
            throw new IllegalArgumentException("When movementIndex equals the number of segments, fraction must be 0.0");
        }

        visibleMovementIndex = movementIndex;
        visibleFractionOfCurrentMovement = fraction;
    }

    /**
     * Paints the turtle's current animated state.
     *
     * Completed filled polygons are painted first, but a fill remains hidden
     * until the movement that completes that polygon has finished animating.
     * Recorded pen-down movements are then rendered as line segments; movements
     * before the current animation position are drawn completely, while the
     * current movement is drawn only through its visible fraction. Pen-up
     * movements affect the turtle's visible position but do not draw a line.
     *
     * The turtle cursor is painted last so that it remains visible above
     * fills and line segments.
     *
     * Turtle coordinates are mapped to Swing coordinates using
     * {@code (width / 2 + x, height / 2 - y)}. Self-intersecting polygon
     * behavior follows Java2D's polygon fill rule.
     *
     * @param g the graphics context used to paint this component
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // Paint completed polygons first.
            
            List<FilledPolygon> filledPolygons = turtle.getFilledPolygons();

            for (FilledPolygon filledPolygon : filledPolygons) {
                // decide whether each completed polygon should be painted.
                int completionIndex = filledPolygon.getCompletionMovementIndex();
                boolean fillIsVisible = visibleMovementIndex > completionIndex
                                        || (visibleMovementIndex == completionIndex 
                                            && visibleFractionOfCurrentMovement >= 1.0);
                if (!fillIsVisible) {
                    continue;
                }

                Polygon polygon = new Polygon();
                for (Vector2D point : filledPolygon.getPoints()) {
                    polygon.addPoint(
                        toScreenX(point.getX()),
                        toScreenY(point.getY())
                    );
                }
                g2d.setColor(filledPolygon.getFillColor());
                g2d.fillPolygon(polygon);
            }

            // Paint recorded LineSegment outlines afterward.

            List<LineSegment> segments = turtle.getSegments();
            List<Movement> movements = turtle.getMovements();
            int segmentIndex = 0;

            for (int i = 0; i < movements.size(); i++) {
                if(!movements.get(i).isPenDown()) {
                    continue;
                }
                LineSegment segment = segments.get(segmentIndex);
                segmentIndex++;
                Vector2D from = segment.getFrom();
                Vector2D to = segment.getTo();

                int x1 = toScreenX(from.getX()); 
                int x2 = toScreenX(from.getX()); // partial x2 no change
                int y1 = toScreenY(from.getY()); 
                int y2 = toScreenY(from.getY()); // partial y2 no change
                double partialX = 0;
                double partialY = 0;
                if (i < visibleMovementIndex) {
                    x2 = toScreenX(to.getX()); 
                    y2 = toScreenY(to.getY()); 
                } else if (i == visibleMovementIndex) {
                    partialX = from.getX() + (to.getX() - from.getX()) * visibleFractionOfCurrentMovement;
                    partialY = from.getY() + (to.getY() - from.getY()) * visibleFractionOfCurrentMovement;
                    x2 = toScreenX(partialX);
                    y2 = toScreenY(partialY);
                } else {
                    continue;
                }

                g2d.setColor(segment.getColor());
                g2d.setStroke(
                    new BasicStroke((float) segment.getWidth())
                );

                g2d.drawLine(x1, y1, x2, y2);
            }
            
            // Paint the turtle cursor last so it appears on top of lines and fills.
            drawVisibleTurtle(g2d);

        } finally {
            g2d.dispose();
        }
    }

    @Override
    public void addNotify(){
        // component becomes displayable
        super.addNotify();
        timer.start();
    }

    @Override
    public void removeNotify() {
        // component stops being displayable
        timer.stop();
        super.removeNotify();
    }

    boolean isAnimationTimerRunning() {
        return timer.isRunning();
    }

    void animationTick() {
        advanceAnimationStep();
        repaint();
    }

    // convert x to screen-x
    private int toScreenX(double turtleX) {
        return (int) Math.round(getWidth() / 2.0 + turtleX);
    }

    // convert y to screen-y
    private int toScreenY(double turtleY) {
        return (int) Math.round(getHeight() / 2.0 - turtleY);
    }
}