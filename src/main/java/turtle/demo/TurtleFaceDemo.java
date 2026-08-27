package turtle.demo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import turtle.Turtle;
import turtle.TurtleCanvas;

/**
 * Animated demonstration of the Turtle library.
 *
 * Draws a complete face while demonstrating:
 *
 * - forward()
 * - backward()
 * - left()
 * - right()
 * - goTo()
 * - home()
 * - setHeading()
 * - penUp()
 * - penDown()
 * - penColor()
 * - penWidth()
 * - fillColor()
 * - beginFill()
 * - endFill()
 * - speed()
 *
 * The Turtle model records all commands immediately. TurtleCanvas then
 * visually animates those recorded movements in order.
 */
public class TurtleFaceDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // ------------------------------------------------------------
            // Create the Turtle model.
            // ------------------------------------------------------------
            Turtle turtle = new Turtle();

            /*
             * IMPORTANT:
             * Create TurtleCanvas before issuing movement commands.
             *
             * Movements that already exist when the canvas is created
             * are considered previously displayed.
             */
            TurtleCanvas canvas = new TurtleCanvas(turtle);

            // ------------------------------------------------------------
            // Create the Swing window.
            // ------------------------------------------------------------
            JFrame frame = new JFrame("Animated Turtle Face Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ------------------------------------------------------------
            // Set animation speed.
            //
            // 0 = immediate
            // 1 = slow
            // 10 = fast
            // ------------------------------------------------------------
            turtle.speed(10);

            // ------------------------------------------------------------
            // Colors used by the drawing.
            // ------------------------------------------------------------
            Color skinColor = new Color(255, 220, 180);
            Color earColor = new Color(245, 195, 160);
            Color eyeColor = Color.WHITE;
            Color pupilColor = new Color(40, 90, 200);
            Color hairColor = new Color(70, 45, 30);

            // ============================================================
            // 1. LEFT EAR
            //
            // Draw the ears before the face so the face overlaps their
            // inner edges.
            // ============================================================

            drawFilledRegularPolygon(
                turtle,
                -155, 0,       // center
                38,            // radius
                12,            // number of sides
                Color.BLACK,   // outline
                earColor,      // fill
                2              // pen width
            );

            // ============================================================
            // 2. RIGHT EAR
            // ============================================================

            drawFilledRegularPolygon(
                turtle,
                155, 0,
                38,
                12,
                Color.BLACK,
                earColor,
                2
            );

            // ============================================================
            // 3. FACE
            //
            // A 24-sided filled polygon approximates a circle.
            // ============================================================

            drawFilledRegularPolygon(
                turtle,
                0, 0,
                150,
                24,
                Color.BLACK,
                skinColor,
                3
            );

            // ============================================================
            // 4. LEFT EYE
            // ============================================================

            drawFilledRegularPolygon(
                turtle,
                -55, 40,
                30,
                16,
                Color.BLACK,
                eyeColor,
                2
            );

            // ============================================================
            // 5. RIGHT EYE
            // ============================================================

            drawFilledRegularPolygon(
                turtle,
                55, 40,
                30,
                16,
                Color.BLACK,
                eyeColor,
                2
            );

            // ============================================================
            // 6. LEFT PUPIL
            // ============================================================

            drawFilledRegularPolygon(
                turtle,
                -55, 40,
                13,
                12,
                Color.BLACK,
                pupilColor,
                1
            );

            // ============================================================
            // 7. RIGHT PUPIL
            // ============================================================

            drawFilledRegularPolygon(
                turtle,
                55, 40,
                13,
                12,
                Color.BLACK,
                pupilColor,
                1
            );

            // ============================================================
            // 8. NOSE
            //
            // Draw a small filled triangular nose.
            // ============================================================

            turtle.penUp();
            turtle.goTo(0, 15);

            turtle.penDown();
            turtle.penColor(Color.BLACK);
            turtle.penWidth(2);

            turtle.fillColor(new Color(235, 175, 135));
            turtle.beginFill();

            turtle.goTo(-18, -20);
            turtle.goTo(18, -20);
            turtle.goTo(0, 15);

            turtle.endFill();

            // ============================================================
            // 9. SMILING MOUTH
            //
            // Draw the smile using a sequence of goTo() movements.
            // ============================================================

            turtle.penUp();
            turtle.goTo(-70, -60);

            turtle.penDown();
            turtle.penColor(Color.RED);
            turtle.penWidth(4);

            turtle.goTo(-55, -72);
            turtle.goTo(-35, -82);
            turtle.goTo(-15, -88);
            turtle.goTo(0, -90);
            turtle.goTo(15, -88);
            turtle.goTo(35, -82);
            turtle.goTo(55, -72);
            turtle.goTo(70, -60);

            // ============================================================
            // 10. LEFT EYEBROW
            //
            // Demonstrates another pen width and color.
            // ============================================================

            turtle.penUp();
            turtle.goTo(-85, 82);

            turtle.penDown();
            turtle.penColor(hairColor);
            turtle.penWidth(5);

            turtle.goTo(-55, 92);
            turtle.goTo(-25, 82);

            // ============================================================
            // 11. RIGHT EYEBROW
            // ============================================================

            turtle.penUp();
            turtle.goTo(25, 82);

            turtle.penDown();

            turtle.goTo(55, 92);
            turtle.goTo(85, 82);

            // ============================================================
            // 12. HAIR
            //
            // Each strand demonstrates:
            //
            // - goTo() with the pen up
            // - setHeading()
            // - forward()
            // - backward()
            //
            // forward() draws outward from the scalp.
            // backward() retraces the same path back to the starting point.
            // ============================================================

            turtle.penColor(hairColor);
            turtle.penWidth(4);

            drawHairStrand(turtle, -105, 110, 125, 48);
            drawHairStrand(turtle,  -80, 130, 110, 55);
            drawHairStrand(turtle,  -50, 142, 100, 58);
            drawHairStrand(turtle,  -20, 148,  95, 62);

            drawHairStrand(turtle,   10, 148,  85, 62);
            drawHairStrand(turtle,   40, 144,  75, 58);
            drawHairStrand(turtle,   70, 133,  65, 55);
            drawHairStrand(turtle,  100, 115,  55, 48);

            // ============================================================
            // 13. SMALL HAIR ZIGZAG
            //
            // Explicitly demonstrates left() and right().
            // ============================================================

            turtle.penUp();
            turtle.goTo(-45, 125);

            turtle.setHeading(20);

            turtle.penDown();
            turtle.penColor(hairColor);
            turtle.penWidth(3);

            turtle.forward(25);
            turtle.left(70);
            turtle.forward(18);
            turtle.right(120);
            turtle.forward(20);
            turtle.left(70);
            turtle.forward(18);

            // ============================================================
            // 14. RETURN HOME
            //
            // home() returns the turtle to (0, 0).
            //
            // Because the pen is up, the turtle should visibly travel
            // back to the center without leaving a line.
            // ============================================================

            turtle.penUp();
            turtle.home();

            // ============================================================
            // 15. FINAL CURSOR HEADING
            //
            // Move slightly upward so the final triangular turtle cursor
            // can be seen pointing toward heading 90 degrees.
            // ============================================================

            turtle.setHeading(90);
            turtle.forward(10);
        });
    }


    /**
     * Draws a filled regular polygon centered at the supplied position.
     *
     * A polygon with many sides can approximate a circle while still
     * exercising Turtle's ordinary movement and fill behavior.
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

        /*
         * Calculate the first vertex.
         *
         * Start at angle 0, which places the first point directly
         * to the right of the polygon's center.
         */
        double firstX = centerX + radius;
        double firstY = centerY;

        // Travel to the first vertex without drawing.
        turtle.penUp();
        turtle.goTo(firstX, firstY);

        // Configure the polygon's appearance.
        turtle.penColor(outlineColor);
        turtle.penWidth(penWidth);
        turtle.fillColor(fillColor);

        // Begin drawing and recording the fill path.
        turtle.penDown();
        turtle.beginFill();

        /*
         * Visit each remaining vertex.
         *
         * The vertices are calculated using sine and cosine around
         * a full 360-degree circle.
         */
        for (int i = 1; i < sides; i++) {

            double angle =
                2.0 * Math.PI * i / sides;

            double x =
                centerX + radius * Math.cos(angle);

            double y =
                centerY + radius * Math.sin(angle);

            turtle.goTo(x, y);
        }

        // Return to the first vertex to close the visible outline.
        turtle.goTo(firstX, firstY);

        // Publish the completed filled polygon.
        turtle.endFill();
    }


    /**
     * Draws one hair strand.
     *
     * The turtle travels to the root with the pen up, points toward the
     * desired heading, draws outward with forward(), and then retraces
     * the strand using backward().
     */
    private static void drawHairStrand(
        Turtle turtle,
        double rootX,
        double rootY,
        double heading,
        double length
    ) {

        // Move to the hair root without drawing.
        turtle.penUp();
        turtle.goTo(rootX, rootY);

        // Point the turtle in the direction of the strand.
        turtle.setHeading(heading);

        // Draw outward.
        turtle.penDown();
        turtle.forward(length);

        /*
         * Move backward along the same heading.
         *
         * backward() does not change the turtle's heading.
         */
        turtle.backward(length);
    }
}