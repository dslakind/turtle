package turtle.demo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import turtle.Turtle;
import turtle.TurtleCanvas;
import turtle.Vector2D;

/**
 * Draws a sunflower whose seeds are positioned using the Golden Ratio.
 *
 * The seed arrangement uses the golden angle:
 *
 *     phi = (1 + sqrt(5)) / 2
 *
 *     golden angle = 360 / phi^2
 *
 *                  ≈ 137.507764 degrees
 *
 * Seed n is placed at:
 *
 *     angle  = n * golden angle
 *     radius = spacing * sqrt(n)
 *
 * This produces the interlocking spiral pattern commonly seen in
 * sunflower seed heads.
 *
 * The demo also draws:
 *
 * - a green stem
 * - two leaves
 * - yellow-orange petals
 * - a dark central seed head
 * - individually filled seeds positioned using the golden angle
 */
public class GoldenRatioSunflowerDemo {

    // ------------------------------------------------------------
    // Golden Ratio constants
    // ------------------------------------------------------------

    private static final double PHI =
        (1.0 + Math.sqrt(5.0)) / 2.0;

    private static final double GOLDEN_ANGLE_DEGREES =
        360.0 / (PHI * PHI);

    // ------------------------------------------------------------
    // Flower location
    // ------------------------------------------------------------

    private static final double FLOWER_X = 0;
    private static final double FLOWER_Y = 70;

    // ------------------------------------------------------------
    // Seed pattern
    // ------------------------------------------------------------

    /*
     * More seeds produce a denser and more realistic pattern.
     *
     * 80  = fairly quick
     * 140 = good detail
     * 200 = dense
     */
    private static final int SEED_COUNT = 160;

    /*
     * Controls how far apart neighboring seeds are.
     */
    private static final double SEED_SPACING = 5.1;

    /*
     * Radius of each individual seed.
     */
    private static final double SEED_RADIUS = 2.8;

    /*
     * A hexagon is sufficient to make these small seeds appear round.
     */
    private static final int SEED_SIDES = 6;

    // ------------------------------------------------------------
    // Petals
    // ------------------------------------------------------------

    private static final int PETAL_COUNT = 20;

    private static final double PETAL_DISTANCE = 105;
    private static final double PETAL_LENGTH = 68;
    private static final double PETAL_WIDTH = 25;

    // ------------------------------------------------------------
    // Colors
    // ------------------------------------------------------------

    private static final Color PETAL_COLOR =
        new Color(255, 190, 20);

    private static final Color PETAL_OUTLINE_COLOR =
        new Color(220, 135, 10);

    private static final Color CENTER_COLOR =
        new Color(75, 45, 20);

    private static final Color SEED_DARK =
        new Color(45, 25, 12);

    private static final Color SEED_GOLD =
        new Color(145, 90, 25);

    private static final Color STEM_COLOR =
        new Color(45, 130, 55);

    private static final Color LEAF_COLOR =
        new Color(65, 155, 70);


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // ------------------------------------------------------------
            // Create the Turtle.
            // ------------------------------------------------------------

            Turtle turtle = new Turtle();

            /*
             * This drawing contains hundreds of movements.
             *
             * Use speed(0) so the completed flower appears quickly.
             *
             * You can try speed(10) with a smaller SEED_COUNT if you
             * want to watch the construction.
             */
            turtle.speed(0);

            /*
             * Generate the drawing before creating the canvas.
             *
             * That causes TurtleCanvas to treat all recorded movements
             * as completed and immediately display the finished flower.
             */
            drawSunflower(turtle);

            // ------------------------------------------------------------
            // Create the canvas.
            // ------------------------------------------------------------

            TurtleCanvas canvas =
                new TurtleCanvas(turtle);

            // ------------------------------------------------------------
            // Create the Swing window.
            // ------------------------------------------------------------

            JFrame frame =
                new JFrame(
                    "Golden Ratio Sunflower"
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
     * Draws the complete sunflower.
     */
    private static void drawSunflower(
        Turtle turtle
    ) {

        // ------------------------------------------------------------
        // 1. STEM
        // ------------------------------------------------------------

        turtle.penUp();
        turtle.goTo(0, -290);

        turtle.penDown();
        turtle.penColor(STEM_COLOR);
        turtle.penWidth(12);

        turtle.goTo(
            FLOWER_X,
            FLOWER_Y - 80
        );


        // ------------------------------------------------------------
        // 2. LEFT LEAF
        // ------------------------------------------------------------

        drawLeaf(
            turtle,
            -10,
            -150,
            -95,
            -105,
            -115,
            -165
        );


        // ------------------------------------------------------------
        // 3. RIGHT LEAF
        // ------------------------------------------------------------

        drawLeaf(
            turtle,
            10,
            -90,
            105,
            -50,
            120,
            -110
        );


        // ------------------------------------------------------------
        // 4. PETALS
        //
        // Place petals evenly around the flower center.
        // ------------------------------------------------------------

        for (int i = 0; i < PETAL_COUNT; i++) {

            double angle =
                360.0 * i / PETAL_COUNT;

            drawPetal(
                turtle,
                FLOWER_X,
                FLOWER_Y,
                angle
            );
        }


        // ------------------------------------------------------------
        // 5. DARK CENTRAL DISK
        //
        // This provides a background behind the individual seeds.
        // ------------------------------------------------------------

        drawFilledRegularPolygon(
            turtle,
            FLOWER_X,
            FLOWER_Y,
            96,
            40,
            CENTER_COLOR,
            CENTER_COLOR,
            1
        );


        // ------------------------------------------------------------
        // 6. GOLDEN-RATIO SEEDS
        //
        // This is the important mathematical part of the demo.
        // ------------------------------------------------------------

        drawGoldenRatioSeeds(turtle);


        // ------------------------------------------------------------
        // Finish with the turtle at the center.
        // ------------------------------------------------------------

        turtle.penUp();
        turtle.home();
        turtle.setHeading(90);
    }


    /**
     * Draws all sunflower seeds using the golden-angle pattern.
     */
    private static void drawGoldenRatioSeeds(
        Turtle turtle
    ) {

        for (int n = 0; n < SEED_COUNT; n++) {

            /*
             * Each new seed is rotated by the golden angle from
             * the previous seed.
             */
            double angleDegrees =
                n * GOLDEN_ANGLE_DEGREES;

            double angleRadians =
                Math.toRadians(angleDegrees);


            /*
             * Move gradually farther from the center.
             *
             * sqrt(n) keeps the seed density approximately uniform
             * as the pattern expands outward.
             */
            double radius =
                SEED_SPACING * Math.sqrt(n);


            /*
             * Convert polar coordinates into x/y coordinates.
             */
            double x =
                FLOWER_X
                + radius * Math.cos(angleRadians);

            double y =
                FLOWER_Y
                + radius * Math.sin(angleRadians);


            /*
             * Alternate two related colors slightly.
             *
             * The positioning is entirely determined by the
             * golden-angle mathematics.
             */
            Color seedColor;

            if (n % 3 == 0) {
                seedColor = SEED_GOLD;
            } else {
                seedColor = SEED_DARK;
            }


            // Draw one small filled seed.
            drawFilledRegularPolygon(
                turtle,
                x,
                y,
                SEED_RADIUS,
                SEED_SIDES,
                seedColor,
                seedColor,
                1
            );
        }
    }


    /**
     * Draws one elongated sunflower petal.
     *
     * The petal is constructed as a narrow symmetric polygon pointing
     * outward from the flower center.
     */
    private static void drawPetal(
        Turtle turtle,
        double centerX,
        double centerY,
        double angleDegrees
    ) {

        double radians =
            Math.toRadians(angleDegrees);

        /*
         * Unit vector pointing outward from the flower.
         */
        double directionX =
            Math.cos(radians);

        double directionY =
            Math.sin(radians);

        /*
         * Perpendicular unit vector.
         *
         * This controls the petal's width.
         */
        double perpendicularX =
            -directionY;

        double perpendicularY =
            directionX;


        /*
         * Center of this petal.
         */
        double petalCenterX =
            centerX
            + PETAL_DISTANCE * directionX;

        double petalCenterY =
            centerY
            + PETAL_DISTANCE * directionY;


        /*
         * Four vertices form a narrow diamond-like petal.
         *
         * inner  -> closest to seed head
         * outer  -> pointed tip
         * left/right -> widest part of petal
         */
        Vector2D inner =
            new Vector2D(
                petalCenterX
                    - PETAL_LENGTH / 2.0 * directionX,

                petalCenterY
                    - PETAL_LENGTH / 2.0 * directionY
            );

        Vector2D outer =
            new Vector2D(
                petalCenterX
                    + PETAL_LENGTH / 2.0 * directionX,

                petalCenterY
                    + PETAL_LENGTH / 2.0 * directionY
            );

        Vector2D left =
            new Vector2D(
                petalCenterX
                    + PETAL_WIDTH / 2.0 * perpendicularX,

                petalCenterY
                    + PETAL_WIDTH / 2.0 * perpendicularY
            );

        Vector2D right =
            new Vector2D(
                petalCenterX
                    - PETAL_WIDTH / 2.0 * perpendicularX,

                petalCenterY
                    - PETAL_WIDTH / 2.0 * perpendicularY
            );


        // Move to the inner petal point.
        turtle.penUp();
        turtle.goTo(
            inner.getX(),
            inner.getY()
        );

        // Configure appearance.
        turtle.penColor(
            PETAL_OUTLINE_COLOR
        );

        turtle.penWidth(1);

        turtle.fillColor(
            PETAL_COLOR
        );

        // Draw and fill the petal.
        turtle.penDown();
        turtle.beginFill();

        turtle.goTo(
            left.getX(),
            left.getY()
        );

        turtle.goTo(
            outer.getX(),
            outer.getY()
        );

        turtle.goTo(
            right.getX(),
            right.getY()
        );

        turtle.goTo(
            inner.getX(),
            inner.getY()
        );

        turtle.endFill();
    }


    /**
     * Draws a simple filled leaf.
     *
     * The six supplied coordinates describe:
     *
     * stem attachment,
     * outer tip,
     * lower control point.
     *
     * A symmetric upper point is calculated to give the leaf shape.
     */
    private static void drawLeaf(
        Turtle turtle,
        double startX,
        double startY,
        double tipX,
        double tipY,
        double lowerX,
        double lowerY
    ) {

        /*
         * Calculate another point on the opposite side of the leaf.
         */
        double upperX =
            startX
            + (tipX - lowerX);

        double upperY =
            startY
            + (tipY - lowerY);


        turtle.penUp();
        turtle.goTo(startX, startY);

        turtle.penColor(STEM_COLOR);
        turtle.penWidth(2);
        turtle.fillColor(LEAF_COLOR);

        turtle.penDown();
        turtle.beginFill();

        turtle.goTo(
            upperX,
            upperY
        );

        turtle.goTo(
            tipX,
            tipY
        );

        turtle.goTo(
            lowerX,
            lowerY
        );

        turtle.goTo(
            startX,
            startY
        );

        turtle.endFill();
    }


    /**
     * Draws a filled regular polygon.
     *
     * For sufficiently many sides this approximates a circle.
     *
     * This helper is used for both the central disk and each individual
     * sunflower seed.
     */
    private static void drawFilledRegularPolygon(
        Turtle turtle,
        double centerX,
        double centerY,
        double radius,
        int sides,
        Color outlineColor,
        Color fillColor,
        double penWidth
    ) {

        // First point lies directly to the right of the center.
        double firstX =
            centerX + radius;

        double firstY =
            centerY;


        // Travel to the first point without drawing.
        turtle.penUp();

        turtle.goTo(
            firstX,
            firstY
        );


        // Configure appearance.
        turtle.penColor(
            outlineColor
        );

        turtle.penWidth(
            penWidth
        );

        turtle.fillColor(
            fillColor
        );


        // Begin the filled shape.
        turtle.penDown();
        turtle.beginFill();


        /*
         * Visit each remaining vertex around the circle.
         */
        for (int i = 1; i < sides; i++) {

            double angle =
                2.0
                * Math.PI
                * i
                / sides;

            double x =
                centerX
                + radius * Math.cos(angle);

            double y =
                centerY
                + radius * Math.sin(angle);

            turtle.goTo(x, y);
        }


        // Close the polygon.
        turtle.goTo(
            firstX,
            firstY
        );

        // Publish the filled polygon.
        turtle.endFill();
    }
}