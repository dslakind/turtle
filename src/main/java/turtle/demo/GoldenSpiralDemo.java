package turtle.demo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import turtle.Turtle;
import turtle.TurtleCanvas;

/**
 * Demonstrates the Fibonacci spiral associated with the Golden Ratio.
 *
 * The drawing consists of adjoining colored squares whose side lengths
 * follow the Fibonacci sequence:
 *
 *     1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89
 *
 * A quarter-circle arc is then drawn inside each square. Together,
 * those arcs form the familiar Fibonacci spiral.
 *
 * As the Fibonacci numbers increase, the ratio of consecutive terms
 * approaches the Golden Ratio:
 *
 *     phi = (1 + sqrt(5)) / 2
 *
 *         ≈ 1.6180339887
 */
public class GoldenSpiralDemo {

    /*
     * Scale the mathematical Fibonacci-square coordinates so the drawing
     * fills a useful portion of the 600 x 600 TurtleCanvas.
     */
    private static final double SCALE = 3.0;

    /*
     * Center the complete collection of Fibonacci squares on the canvas.
     */
    private static final double CENTER_X = 20.5;
    private static final double CENTER_Y = 33.0;

    /*
     * Number of straight line segments used to approximate each
     * quarter-circle.
     *
     * Increase this for smoother arcs.
     */
    private static final int ARC_SEGMENTS = 12;

    /*
     * Each row contains:
     *
     *     { x, y, sideLength }
     *
     * x and y identify the lower-left corner of the square.
     */
    private static final double[][] SQUARES = {

        // x      y      size

        {   0,    0,      1 },
        {   1,    0,      1 },
        {   0,    1,      2 },
        {  -3,    0,      3 },
        {  -3,   -5,      5 },
        {   2,   -5,      8 },
        {  -3,    3,     13 },
        { -24,   -5,     21 },
        { -24,  -39,     34 },
        {  10,  -39,     55 },
        { -24,   16,     89 }
    };

    /*
     * Give every Fibonacci square its own fill color.
     *
     * The colors are deliberately fairly light so that the black
     * square outlines and the spiral remain easy to see.
     */
    private static final Color[] SQUARE_COLORS = {

        new Color(255, 204, 204),  // light red
        new Color(255, 229, 180),  // light orange
        new Color(255, 255, 180),  // light yellow
        new Color(204, 255, 204),  // light green
        new Color(180, 235, 220),  // mint
        new Color(180, 220, 255),  // light blue
        new Color(204, 204, 255),  // lavender blue
        new Color(225, 190, 255),  // lavender
        new Color(255, 190, 235),  // pink
        new Color(220, 220, 220),  // light gray
        new Color(210, 240, 190)   // pale green
    };

    /*
     * Each row contains:
     *
     *     { centerX, centerY, radius, startingAngle }
     *
     * Every arc sweeps counterclockwise through 90 degrees.
     */
    private static final double[][] ARCS = {

        // centerX centerY radius startAngle

        {   1,    1,      1,    180 },
        {   1,    1,      1,    270 },
        {   0,    1,      2,      0 },
        {   0,    0,      3,     90 },
        {   2,    0,      5,    180 },
        {   2,    3,      8,    270 },
        {  -3,    3,     13,      0 },
        {  -3,   -5,     21,     90 },
        {  10,   -5,     34,    180 },
        {  10,   16,     55,    270 },
        { -24,   16,     89,      0 }
    };


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // ------------------------------------------------------------
            // Create the turtle and canvas.
            // ------------------------------------------------------------

            Turtle turtle = new Turtle();

            /*
             * Create the canvas BEFORE issuing drawing commands so the
             * construction can be animated.
             */
            TurtleCanvas canvas = new TurtleCanvas(turtle);

            // ------------------------------------------------------------
            // Create the Swing window.
            // ------------------------------------------------------------

            JFrame frame =
                new JFrame("Colored Fibonacci / Golden Ratio Spiral");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(canvas);
            frame.pack();

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ------------------------------------------------------------
            // Animation speed.
            // ------------------------------------------------------------

            /*
             * Try values from 1 through 10.
             *
             * Use 0 if you want the completed drawing to appear
             * immediately.
             */
            turtle.speed(10);

            // ------------------------------------------------------------
            // Draw and fill the Fibonacci squares.
            // ------------------------------------------------------------

            for (int i = 0; i < SQUARES.length; i++) {

                double x = SQUARES[i][0];
                double y = SQUARES[i][1];
                double size = SQUARES[i][2];

                Color fillColor = SQUARE_COLORS[i];

                drawFilledSquare(
                    turtle,
                    x,
                    y,
                    size,
                    fillColor
                );
            }

            // ------------------------------------------------------------
            // Draw the Golden Ratio / Fibonacci spiral afterward.
            //
            // Drawing it last ensures that the spiral appears on top of
            // all of the colored squares.
            // ------------------------------------------------------------

            turtle.penColor(Color.BLACK);
            turtle.penWidth(3);

            for (double[] arc : ARCS) {

                double centerX = arc[0];
                double centerY = arc[1];
                double radius = arc[2];
                double startAngle = arc[3];

                drawQuarterCircle(
                    turtle,
                    centerX,
                    centerY,
                    radius,
                    startAngle
                );
            }

            // ------------------------------------------------------------
            // Leave the turtle at the origin without drawing.
            // ------------------------------------------------------------

            turtle.penUp();
            turtle.home();
            turtle.setHeading(0);
        });
    }


    /**
     * Draws and fills one Fibonacci square.
     *
     * The supplied x and y coordinates identify the lower-left corner
     * of the square in the unscaled Fibonacci coordinate system.
     */
    private static void drawFilledSquare(
        Turtle turtle,
        double x,
        double y,
        double size,
        Color fillColor
    ) {

        // Convert the square corners into TurtleCanvas coordinates.
        double left = transformX(x);
        double right = transformX(x + size);

        double bottom = transformY(y);
        double top = transformY(y + size);

        // Move to the lower-left corner without drawing.
        turtle.penUp();
        turtle.goTo(left, bottom);

        // Configure the square's appearance.
        turtle.penColor(Color.BLACK);
        turtle.penWidth(1);
        turtle.fillColor(fillColor);

        /*
         * Begin recording the square as a filled polygon.
         *
         * With the animation support currently implemented in TurtleCanvas,
         * the fill will not appear until the movements belonging to this
         * square have completed visually.
         */
        turtle.penDown();
        turtle.beginFill();

        // Draw the four sides of the square.
        turtle.goTo(right, bottom);
        turtle.goTo(right, top);
        turtle.goTo(left, top);
        turtle.goTo(left, bottom);

        // Complete and publish the filled square.
        turtle.endFill();
    }


    /**
     * Draws one 90-degree circular arc.
     *
     * Turtle currently has no built-in circle or arc command, so the curve
     * is approximated using several short LineSegments.
     */
    private static void drawQuarterCircle(
        Turtle turtle,
        double centerX,
        double centerY,
        double radius,
        double startAngle
    ) {

        // Calculate the first point on the quarter-circle.
        double startRadians =
            Math.toRadians(startAngle);

        double firstX =
            centerX
            + radius * Math.cos(startRadians);

        double firstY =
            centerY
            + radius * Math.sin(startRadians);

        // Move to the beginning of the arc without drawing.
        turtle.penUp();

        turtle.goTo(
            transformX(firstX),
            transformY(firstY)
        );

        // Draw the remainder of the arc.
        turtle.penDown();

        /*
         * Sweep counterclockwise through 90 degrees.
         *
         * Each iteration draws one small piece of the curve.
         */
        for (int i = 1; i <= ARC_SEGMENTS; i++) {

            double fraction =
                (double) i / ARC_SEGMENTS;

            double angle =
                startAngle + 90.0 * fraction;

            double radians =
                Math.toRadians(angle);

            double x =
                centerX
                + radius * Math.cos(radians);

            double y =
                centerY
                + radius * Math.sin(radians);

            turtle.goTo(
                transformX(x),
                transformY(y)
            );
        }
    }


    /**
     * Converts an unscaled Fibonacci x-coordinate into a centered
     * TurtleCanvas coordinate.
     */
    private static double transformX(double x) {

        return (x - CENTER_X) * SCALE;
    }


    /**
     * Converts an unscaled Fibonacci y-coordinate into a centered
     * TurtleCanvas coordinate.
     */
    private static double transformY(double y) {

        return (y - CENTER_Y) * SCALE;
    }
}