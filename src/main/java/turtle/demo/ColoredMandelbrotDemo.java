package turtle.demo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import turtle.Turtle;
import turtle.TurtleCanvas;

/**
 * Displays a colored approximation of the Mandelbrot Set.
 *
 * Each point in the complex plane is tested using:
 *
 *     z(n + 1) = z(n)^2 + c
 *
 * with:
 *
 *     z(0) = 0
 *
 * The color of each point depends on how many iterations occur
 * before the sequence escapes.
 *
 * Points that do NOT escape within MAX_ITERATIONS are drawn black.
 *
 * Escaping points are grouped into iteration bands:
 *
 *      0 -  9 iterations
 *     10 - 19 iterations
 *     20 - 29 iterations
 *     30 - 39 iterations
 *     ...
 *
 * Each band receives a different color.
 */
public class ColoredMandelbrotDemo {

    // ------------------------------------------------------------
    // Drawing size
    // ------------------------------------------------------------

    /*
     * Leave a small margin around the edge of the 600 x 600 canvas.
     */
    private static final int DRAW_WIDTH = 560;
    private static final int DRAW_HEIGHT = 560;

    /*
     * Sampling distance in Turtle coordinate units.
     *
     * Smaller values give more detail but create more movements.
     *
     * Try:
     *
     * 4.0 = coarse and fast
     * 3.0 = good starting point
     * 2.0 = detailed
     * 1.0 = much more expensive
     */
    private static final double STEP = 1.0;


    // ------------------------------------------------------------
    // Mandelbrot settings
    // ------------------------------------------------------------

    /*
     * More iterations reveal more detail near the boundary.
     */
    private static final int MAX_ITERATIONS = 500;

    /*
     * Each group of 10 escape iterations gets its own color.
     */
    private static final int COLOR_BAND_SIZE = 10;


    // ------------------------------------------------------------
    // Complex-plane region
    // ------------------------------------------------------------

    private static final double MIN_REAL = -2.1;
    private static final double MAX_REAL = 0.8;

    private static final double MIN_IMAGINARY = -1.2;
    private static final double MAX_IMAGINARY = 1.2;


    // ------------------------------------------------------------
    // Escape colors
    // ------------------------------------------------------------

    /*
     * Colors are ordered from fast escape to slow escape.
     *
     * Since COLOR_BAND_SIZE is 10:
     *
     * COLORS[0] -> escape during iterations 0-9
     * COLORS[1] -> escape during iterations 10-19
     * COLORS[2] -> escape during iterations 20-29
     * ...
     *
     * Points that never escape are NOT taken from this array;
     * they are always black.
     */
    private static final Color[] ESCAPE_COLORS = {

        new Color(20, 20, 90),      // deep navy
        new Color(25, 40, 130),     // dark blue
        new Color(30, 65, 170),     // blue
        new Color(35, 95, 200),     // bright blue
        new Color(40, 130, 220),    // sky blue
        new Color(35, 165, 220),    // cyan-blue
        new Color(30, 190, 200),    // cyan
        new Color(30, 200, 160),    // turquoise
        new Color(40, 195, 110),    // green
        new Color(90, 195, 70),     // yellow-green
        new Color(150, 205, 55),    // lime-yellow
        new Color(205, 210, 45),    // yellow
        new Color(245, 200, 35),    // golden yellow
        new Color(250, 165, 30),    // orange
        new Color(245, 125, 30),    // deep orange
        new Color(235, 85, 35),     // orange-red
        new Color(210, 55, 50),     // red
        new Color(185, 45, 80),     // crimson
        new Color(160, 40, 120),    // magenta-purple
        new Color(125, 45, 165),    // purple
        new Color(90, 55, 190),     // violet
        new Color(65, 65, 200),     // indigo
        new Color(45, 80, 190),     // blue-violet
        new Color(30, 100, 170)     // return toward blue
    };


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // ------------------------------------------------------------
            // Create the Turtle model.
            // ------------------------------------------------------------

            Turtle turtle = new Turtle();

            /*
             * Mandelbrot rendering creates thousands of movements.
             *
             * We do not want to animate every individual raster line.
             */
            turtle.speed(0);

            // ------------------------------------------------------------
            // Generate the Mandelbrot drawing.
            // ------------------------------------------------------------

            drawMandelbrot(turtle);

            /*
             * Create the canvas AFTER drawing.
             *
             * All recorded movements therefore start in the completed
             * state and the image appears immediately.
             */
            TurtleCanvas canvas =
                new TurtleCanvas(turtle);


            // ------------------------------------------------------------
            // Create the Swing window.
            // ------------------------------------------------------------

            JFrame frame =
                new JFrame(
                    "Colored Mandelbrot Set"
                );

            frame.setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
            );

            frame.add(canvas);
            frame.pack();

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


    /**
     * Draws the Mandelbrot Set as horizontal colored runs.
     *
     * Neighboring sample points that have the same color are combined
     * into one line segment. This requires substantially fewer Turtle
     * movements than drawing every sample individually.
     */
    private static void drawMandelbrot(
        Turtle turtle
    ) {

        /*
         * Make each horizontal line approximately as thick as one
         * sampling row so the collection of runs looks like a raster image.
         */
        turtle.penWidth(STEP);


        // ------------------------------------------------------------
        // Process one horizontal row at a time.
        // ------------------------------------------------------------

        for (
            double screenY = DRAW_HEIGHT / 2.0;
            screenY >= -DRAW_HEIGHT / 2.0;
            screenY -= STEP
        ) {

            Color currentColor = null;

            double runStartX =
                -DRAW_WIDTH / 2.0;


            // --------------------------------------------------------
            // Scan from left to right.
            // --------------------------------------------------------

            for (
                double screenX = -DRAW_WIDTH / 2.0;
                screenX <= DRAW_WIDTH / 2.0;
                screenX += STEP
            ) {

                // ----------------------------------------------------
                // Convert screen position into the complex plane.
                // ----------------------------------------------------

                double cReal =
                    map(
                        screenX,
                        -DRAW_WIDTH / 2.0,
                        DRAW_WIDTH / 2.0,
                        MIN_REAL,
                        MAX_REAL
                    );

                double cImaginary =
                    map(
                        screenY,
                        -DRAW_HEIGHT / 2.0,
                        DRAW_HEIGHT / 2.0,
                        MIN_IMAGINARY,
                        MAX_IMAGINARY
                    );


                // ----------------------------------------------------
                // Determine when this point escapes.
                // ----------------------------------------------------

                int iterations =
                    escapeIterations(
                        cReal,
                        cImaginary,
                        MAX_ITERATIONS
                    );


                // ----------------------------------------------------
                // Convert the iteration count into a color.
                // ----------------------------------------------------

                Color pointColor =
                    colorForIterations(
                        iterations
                    );


                /*
                 * Start the first run in this row.
                 */
                if (currentColor == null) {

                    currentColor =
                        pointColor;

                    runStartX =
                        screenX;

                    continue;
                }


                /*
                 * When the color changes, finish the previous run.
                 */
                if (!pointColor.equals(currentColor)) {

                    drawHorizontalRun(
                        turtle,
                        runStartX,
                        screenX - STEP,
                        screenY,
                        currentColor
                    );

                    /*
                     * Begin the next run.
                     */
                    runStartX =
                        screenX;

                    currentColor =
                        pointColor;
                }
            }


            /*
             * Finish the last run in the row.
             */
            drawHorizontalRun(
                turtle,
                runStartX,
                DRAW_WIDTH / 2.0,
                screenY,
                currentColor
            );
        }


        // ------------------------------------------------------------
        // Leave the Turtle at the origin.
        // ------------------------------------------------------------

        turtle.penUp();
        turtle.home();
    }


    /**
     * Returns the number of iterations required for a Mandelbrot
     * sequence to escape.
     *
     * If the point has NOT escaped after maxIterations, this method
     * returns maxIterations.
     *
     * Therefore:
     *
     *     result < maxIterations
     *
     * means the point escaped.
     *
     *     result == maxIterations
     *
     * means the point is treated as belonging to the Mandelbrot Set.
     */
    private static int escapeIterations(
        double cReal,
        double cImaginary,
        int maxIterations
    ) {

        double zReal = 0.0;
        double zImaginary = 0.0;


        for (
            int iteration = 0;
            iteration < maxIterations;
            iteration++
        ) {

            /*
             * Calculate:
             *
             *     z^2 + c
             *
             * where:
             *
             *     z = a + bi
             *
             * and:
             *
             *     z^2 =
             *
             *     (a^2 - b^2)
             *     +
             *     (2ab)i
             */
            double nextReal =
                zReal * zReal
                - zImaginary * zImaginary
                + cReal;

            double nextImaginary =
                2.0
                * zReal
                * zImaginary
                + cImaginary;


            zReal =
                nextReal;

            zImaginary =
                nextImaginary;


            /*
             * A Mandelbrot sequence is guaranteed to diverge once:
             *
             *     |z| > 2
             *
             * Rather than calculating the square root, compare:
             *
             *     real^2 + imaginary^2 > 4
             */
            double magnitudeSquared =
                zReal * zReal
                + zImaginary * zImaginary;


            if (magnitudeSquared > 4.0) {

                /*
                 * Add one because humans normally describe this as
                 * "escaped after N iterations" rather than beginning
                 * the count at zero.
                 */
                return iteration + 1;
            }
        }


        /*
         * The sequence did not escape within our iteration limit.
         */
        return maxIterations;
    }


    /**
     * Converts an escape iteration count into a display color.
     */
    private static Color colorForIterations(
        int iterations
    ) {

        // ------------------------------------------------------------
        // Points that do not diverge are black.
        // ------------------------------------------------------------

        if (iterations >= MAX_ITERATIONS) {
            return Color.BLACK;
        }


        // ------------------------------------------------------------
        // Find the iteration band.
        //
        // Examples with COLOR_BAND_SIZE = 10:
        //
        //      7 iterations -> band 0
        //     15 iterations -> band 1
        //     23 iterations -> band 2
        //     37 iterations -> band 3
        // ------------------------------------------------------------

        int band =
            iterations / COLOR_BAND_SIZE;


        /*
         * If MAX_ITERATIONS is ever increased beyond the available
         * colors, cycle through the palette.
         */
        int colorIndex =
            band % ESCAPE_COLORS.length;


        return ESCAPE_COLORS[colorIndex];
    }


    /**
     * Draws one horizontal run in one color.
     */
    private static void drawHorizontalRun(
        Turtle turtle,
        double startX,
        double endX,
        double y,
        Color color
    ) {

        // ------------------------------------------------------------
        // Move to the beginning without drawing.
        // ------------------------------------------------------------

        turtle.penUp();

        turtle.goTo(
            startX,
            y
        );


        // ------------------------------------------------------------
        // Draw the colored run.
        // ------------------------------------------------------------

        turtle.penColor(color);

        turtle.penDown();

        turtle.goTo(
            endX,
            y
        );
    }


    /**
     * Maps a number from one numerical range into another.
     *
     * This is used to convert Turtle screen coordinates into
     * coordinates in the complex plane.
     */
    private static double map(
        double value,
        double sourceMin,
        double sourceMax,
        double destinationMin,
        double destinationMax
    ) {

        double fraction =
            (value - sourceMin)
            / (sourceMax - sourceMin);


        return destinationMin
            + fraction
            * (destinationMax - destinationMin);
    }
}