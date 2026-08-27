package turtle.demo;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import turtle.Turtle;
import turtle.TurtleCanvas;
import turtle.Vector2D;

/**
 * Animated, step-by-step construction of the Koch Snowflake.
 *
 * The demo follows the traditional hand-construction process:
 *
 * 1. Start with an equilateral triangle.
 *
 * 2. For every line segment:
 *      a. Divide the segment into three equal parts.
 *      b. Use the middle third as the base of a new outward-pointing
 *         equilateral triangle.
 *      c. Draw that complete triangle.
 *      d. Erase the middle-third base.
 *
 *    The original line segment has now been replaced by four smaller
 *    line segments.
 *
 * 3. Repeat the same process on every new line segment.
 *
 * Each iteration uses a different boundary color so that the recursive
 * development of the snowflake is easier to see.
 */
public class KochSnowflakeConstructionDemo {

    /*
     * Number of complete Koch iterations.
     *
     * ITERATIONS = 0 -> original triangle
     * ITERATIONS = 1 -> first Koch iteration
     * ITERATIONS = 2 -> second Koch iteration
     * ITERATIONS = 3 -> substantially more detailed
     *
     * Two iterations are recommended when watching the actual
     * construction process.
     */
    private static final int ITERATIONS = 2;

    /*
     * Animation speed.
     *
     * 1  = slow
     * 10 = fast
     * 0  = immediate
     */
    private static final int ANIMATION_SPEED = 8;

    /*
     * Colors used to make the construction process visible.
     */
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    /*
     * The temporary construction triangle is drawn in orange.
     */
    private static final Color CONSTRUCTION_COLOR =
        new Color(230, 120, 30);

    /*
     * Each completed iteration gets its own color.
     *
     * Index 0 is the original triangle.
     * Index 1 is the first Koch iteration.
     * Index 2 is the second Koch iteration.
     * Index 3 is the third Koch iteration.
     */
    private static final Color[] ITERATION_COLORS = {
        Color.BLACK,
        new Color(40, 100, 200),
        new Color(130, 60, 180),
        new Color(20, 140, 100)
    };

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // ------------------------------------------------------------
            // Create the Turtle and canvas.
            // ------------------------------------------------------------

            Turtle turtle = new Turtle();

            /*
             * Create the canvas before issuing drawing commands.
             *
             * This allows TurtleCanvas to animate every movement that
             * follows.
             */
            TurtleCanvas canvas = new TurtleCanvas(turtle);

            // ------------------------------------------------------------
            // Create the Swing window.
            // ------------------------------------------------------------

            JFrame frame =
                new JFrame("Step-by-Step Koch Snowflake Construction");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(canvas);
            frame.pack();

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ------------------------------------------------------------
            // Configure animation.
            // ------------------------------------------------------------

            turtle.speed(ANIMATION_SPEED);

            // ------------------------------------------------------------
            // Define the original equilateral triangle.
            //
            // The vertices are ordered counterclockwise.
            //
            // Keeping the boundary counterclockwise is important because
            // the outward Koch bump will always be constructed on the
            // right side of each directed line segment.
            // ------------------------------------------------------------

            Vector2D left =
                new Vector2D(-180, -105);

            Vector2D right =
                new Vector2D(180, -105);

            Vector2D top =
                new Vector2D(0, 207);

            // ------------------------------------------------------------
            // ITERATION 0
            //
            // Start with the three sides of one equilateral triangle.
            // ------------------------------------------------------------

            List<BoundarySegment> boundary =
                new ArrayList<>();

            boundary.add(
                new BoundarySegment(left, right)
            );

            boundary.add(
                new BoundarySegment(right, top)
            );

            boundary.add(
                new BoundarySegment(top, left)
            );

            // Draw the starting triangle.
            traceBoundary(
                turtle,
                boundary,
                ITERATION_COLORS[0],
                2
            );

            // ------------------------------------------------------------
            // Perform each complete Koch iteration.
            // ------------------------------------------------------------

            for (int iteration = 1;
                 iteration <= ITERATIONS;
                 iteration++) {

                Color iterationColor =
                    ITERATION_COLORS[
                        Math.min(
                            iteration,
                            ITERATION_COLORS.length - 1
                        )
                    ];

                /*
                 * Replace every segment in the current boundary according
                 * to the Koch construction rule.
                 */
                boundary = constructNextIteration(
                    turtle,
                    boundary,
                    iterationColor
                );

                /*
                 * Retrace the complete new boundary.
                 *
                 * This provides a visual "snapshot" of the completed
                 * iteration before construction of the next iteration
                 * begins.
                 */
                traceBoundary(
                    turtle,
                    boundary,
                    iterationColor,
                    2
                );
            }

            // ------------------------------------------------------------
            // Finish with the turtle in the center.
            // ------------------------------------------------------------

            turtle.penUp();
            turtle.home();
            turtle.setHeading(90);
        });
    }


    /**
     * Constructs one complete Koch iteration.
     *
     * Every segment in the current boundary is replaced by four
     * smaller segments.
     */
    private static List<BoundarySegment> constructNextIteration(
        Turtle turtle,
        List<BoundarySegment> currentBoundary,
        Color iterationColor
    ) {

        List<BoundarySegment> nextBoundary =
            new ArrayList<>();

        /*
         * Perform the hand-construction rule separately on every
         * current line segment.
         */
        for (BoundarySegment segment : currentBoundary) {

            Vector2D start =
                segment.getStart();

            Vector2D end =
                segment.getEnd();


            // ------------------------------------------------------------
            // STEP 1
            //
            // Divide the line into three equal parts.
            // ------------------------------------------------------------

            Vector2D oneThird =
                pointOneThird(start, end);

            Vector2D twoThirds =
                pointTwoThirds(start, end);


            /*
             * Visually highlight the middle third.
             *
             * This makes the subdivision step visible before the
             * equilateral triangle is constructed.
             */
            drawLine(
                turtle,
                oneThird,
                twoThirds,
                CONSTRUCTION_COLOR,
                4
            );


            // ------------------------------------------------------------
            // STEP 2
            //
            // Construct the apex of an equilateral triangle whose base
            // is the middle third.
            // ------------------------------------------------------------

            Vector2D peak =
                outwardPeak(
                    oneThird,
                    twoThirds
                );


            /*
             * Draw the COMPLETE temporary equilateral triangle.
             *
             * At this moment the viewer should see:
             *
             *             peak
             *              /\
             *             /  \
             *            /____\
             *
             *        oneThird  twoThirds
             *
             * The horizontal/base segment still exists.
             */
            drawLine(
                turtle,
                oneThird,
                peak,
                CONSTRUCTION_COLOR,
                3
            );

            drawLine(
                turtle,
                peak,
                twoThirds,
                CONSTRUCTION_COLOR,
                3
            );

            drawLine(
                turtle,
                twoThirds,
                oneThird,
                CONSTRUCTION_COLOR,
                3
            );


            // ------------------------------------------------------------
            // STEP 3
            //
            // Remove the base of the newly constructed triangle.
            //
            // Since TurtleCanvas has a white background, drawing a
            // slightly wider white line over the middle third behaves
            // like erasing that line by hand.
            // ------------------------------------------------------------

            drawLine(
                turtle,
                oneThird,
                twoThirds,
                BACKGROUND_COLOR,
                6
            );


            // ------------------------------------------------------------
            // The original segment has now effectively been replaced by:
            //
            //     start ------ oneThird
            //                      \
            //                       peak
            //                      /
            //             twoThirds ------ end
            //
            // Trace those four new boundary segments in the color for
            // this iteration.
            // ------------------------------------------------------------

            drawLine(
                turtle,
                start,
                oneThird,
                iterationColor,
                2
            );

            drawLine(
                turtle,
                oneThird,
                peak,
                iterationColor,
                2
            );

            drawLine(
                turtle,
                peak,
                twoThirds,
                iterationColor,
                2
            );

            drawLine(
                turtle,
                twoThirds,
                end,
                iterationColor,
                2
            );


            // ------------------------------------------------------------
            // Save those four segments for the next iteration.
            // ------------------------------------------------------------

            nextBoundary.add(
                new BoundarySegment(
                    start,
                    oneThird
                )
            );

            nextBoundary.add(
                new BoundarySegment(
                    oneThird,
                    peak
                )
            );

            nextBoundary.add(
                new BoundarySegment(
                    peak,
                    twoThirds
                )
            );

            nextBoundary.add(
                new BoundarySegment(
                    twoThirds,
                    end
                )
            );
        }

        return nextBoundary;
    }


    /**
     * Retraces an entire completed Koch boundary.
     *
     * This makes the completed result of each iteration easy to see
     * before the next iteration begins.
     */
    private static void traceBoundary(
        Turtle turtle,
        List<BoundarySegment> boundary,
        Color color,
        double penWidth
    ) {

        if (boundary.isEmpty()) {
            return;
        }

        // Move to the beginning without drawing.
        Vector2D first =
            boundary.get(0).getStart();

        moveTo(
            turtle,
            first
        );

        /*
         * Draw the complete boundary in sequence.
         */
        for (BoundarySegment segment : boundary) {

            drawLine(
                turtle,
                segment.getStart(),
                segment.getEnd(),
                color,
                penWidth
            );
        }
    }


    /**
     * Draws a line between two points using the requested appearance.
     *
     * The turtle heading is set to the direction of travel before
     * each movement so the triangular turtle cursor points along
     * the line it is drawing.
     */
    private static void drawLine(
        Turtle turtle,
        Vector2D start,
        Vector2D end,
        Color color,
        double penWidth
    ) {

        /*
         * Move to the beginning of the line without drawing if the
         * turtle is not already there.
         */
        turtle.penUp();

        pointToward(
            turtle,
            turtle.getPosition(),
            start
        );

        turtle.goTo(
            start.getX(),
            start.getY()
        );

        // Configure the line.
        turtle.penColor(color);
        turtle.penWidth(penWidth);

        // Point toward the destination.
        pointToward(
            turtle,
            start,
            end
        );

        // Draw the line.
        turtle.penDown();

        turtle.goTo(
            end.getX(),
            end.getY()
        );
    }


    /**
     * Moves to a point without drawing.
     */
    private static void moveTo(
        Turtle turtle,
        Vector2D destination
    ) {

        turtle.penUp();

        pointToward(
            turtle,
            turtle.getPosition(),
            destination
        );

        turtle.goTo(
            destination.getX(),
            destination.getY()
        );
    }


    /**
     * Sets the turtle heading so that it points from one coordinate
     * toward another coordinate.
     */
    private static void pointToward(
        Turtle turtle,
        Vector2D from,
        Vector2D to
    ) {

        double dx =
            to.getX() - from.getX();

        double dy =
            to.getY() - from.getY();

        /*
         * Avoid changing the heading when both points are identical.
         */
        if (Math.abs(dx) < 1e-9
            && Math.abs(dy) < 1e-9) {

            return;
        }

        double heading =
            Math.toDegrees(
                Math.atan2(dy, dx)
            );

        /*
         * Normalize negative angles to the Turtle convention
         * of 0 through 360 degrees.
         */
        if (heading < 0) {
            heading += 360;
        }

        turtle.setHeading(heading);
    }


    /**
     * Returns the point exactly one-third of the way from start to end.
     */
    private static Vector2D pointOneThird(
        Vector2D start,
        Vector2D end
    ) {

        double x =
            start.getX()
            + (end.getX() - start.getX()) / 3.0;

        double y =
            start.getY()
            + (end.getY() - start.getY()) / 3.0;

        return new Vector2D(x, y);
    }


    /**
     * Returns the point exactly two-thirds of the way from start to end.
     */
    private static Vector2D pointTwoThirds(
        Vector2D start,
        Vector2D end
    ) {

        double x =
            start.getX()
            + 2.0
            * (end.getX() - start.getX())
            / 3.0;

        double y =
            start.getY()
            + 2.0
            * (end.getY() - start.getY())
            / 3.0;

        return new Vector2D(x, y);
    }


    /**
     * Calculates the apex of the outward-pointing equilateral triangle
     * constructed on the middle third of a Koch segment.
     *
     * The main boundary is stored counterclockwise.
     *
     * Therefore, the interior of the snowflake is on the LEFT side
     * of each directed segment and the outward direction is on the RIGHT.
     *
     * Rotating the middle-third vector by -60 degrees places the
     * triangular bump on that outward side.
     */
    private static Vector2D outwardPeak(
        Vector2D oneThird,
        Vector2D twoThirds
    ) {

        double dx =
            twoThirds.getX()
            - oneThird.getX();

        double dy =
            twoThirds.getY()
            - oneThird.getY();

        double radians =
            Math.toRadians(-60.0);

        double cos =
            Math.cos(radians);

        double sin =
            Math.sin(radians);

        double rotatedX =
            dx * cos
            - dy * sin;

        double rotatedY =
            dx * sin
            + dy * cos;

        return new Vector2D(
            oneThird.getX() + rotatedX,
            oneThird.getY() + rotatedY
        );
    }


    /**
     * Small data object representing one directed boundary segment
     * of the current Koch iteration.
     */
    private static class BoundarySegment {

        private final Vector2D start;
        private final Vector2D end;

        BoundarySegment(
            Vector2D initStart,
            Vector2D initEnd
        ) {

            start =
                new Vector2D(initStart);

            end =
                new Vector2D(initEnd);
        }

        Vector2D getStart() {
            return new Vector2D(start);
        }

        Vector2D getEnd() {
            return new Vector2D(end);
        }
    }
}