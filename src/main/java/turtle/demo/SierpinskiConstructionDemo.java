package turtle.demo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import turtle.Turtle;
import turtle.TurtleCanvas;
import turtle.Vector2D;

/**
 * Demonstrates the Sierpinski Triangle by showing the actual
 * recursive construction process:
 *
 * 1. Start with one large filled equilateral triangle.
 * 2. Find the midpoints of its three sides.
 * 3. Connect those midpoints to form an upside-down triangle.
 * 4. Remove that central triangle.
 * 5. Repeat the same process on the three remaining upright triangles.
 *
 * This version is more faithful to the standard geometric construction
 * than a demo that only draws the final small triangles.
 */
public class SierpinskiConstructionDemo {

    /*
     * Use 3 or 4 if you want to clearly watch the recursive steps.
     *
     * depth 1 -> remove 1 central triangle
     * depth 2 -> remove 4 central triangles total
     * depth 3 -> remove 13 central triangles total
     * depth 4 -> more detailed, but busier
     */
    private static final int RECURSION_DEPTH = 3;

    /*
     * Animation speed:
     * 0  = immediate
     * 1  = slow
     * 10 = fast
     */
    private static final int ANIMATION_SPEED = 6;

    // Colors
    private static final Color OUTLINE_COLOR = Color.BLACK;
    private static final Color BASE_FILL_COLOR = new Color(70, 130, 220);
    private static final Color CUT_GUIDE_COLOR = new Color(220, 60, 60);
    private static final Color REMOVED_COLOR = Color.WHITE;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Create the turtle model.
            Turtle turtle = new Turtle();

            // Create the canvas before issuing movement commands.
            TurtleCanvas canvas = new TurtleCanvas(turtle);

            // Create the Swing window.
            JFrame frame = new JFrame("Sierpinski Triangle Construction Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Configure animation and default appearance.
            turtle.speed(ANIMATION_SPEED);
            turtle.penColor(OUTLINE_COLOR);
            turtle.penWidth(2);

            /*
             * Define one large equilateral triangle.
             *
             * Side length is approximately 500.
             * Height is approximately 433.
             */
            Vector2D left = new Vector2D(-250, -170);
            Vector2D right = new Vector2D(250, -170);
            Vector2D top = new Vector2D(0, 263);

            // ------------------------------------------------------------
            // Step 1: Start with one large filled equilateral triangle.
            // ------------------------------------------------------------
            fillTriangle(
                turtle,
                left,
                right,
                top,
                OUTLINE_COLOR,
                BASE_FILL_COLOR,
                2
            );

            // ------------------------------------------------------------
            // Steps 2–5: Recursively construct the Sierpinski triangle.
            // ------------------------------------------------------------
            constructSierpinski(
                turtle,
                left,
                right,
                top,
                RECURSION_DEPTH
            );

            // Return the turtle to the center without drawing.
            turtle.penUp();
            turtle.home();
            turtle.setHeading(90);
        });
    }

    /**
     * Recursively performs the geometric Sierpinski construction.
     *
     * For the current upright triangle:
     *
     * 2. Find side midpoints.
     * 3. Connect them to form the central inverted triangle.
     * 4. Remove that central triangle.
     * 5. Repeat on the three remaining upright triangles.
     */
    private static void constructSierpinski(
        Turtle turtle,
        Vector2D left,
        Vector2D right,
        Vector2D top,
        int depth
    ) {
        // Base case: stop recursing.
        if (depth == 0) {
            return;
        }

        // ------------------------------------------------------------
        // Step 2: Find the middle point of each side.
        // ------------------------------------------------------------
        Vector2D leftTopMid = midpoint(left, top);
        Vector2D rightTopMid = midpoint(right, top);
        Vector2D bottomMid = midpoint(left, right);

        // ------------------------------------------------------------
        // Step 3: Connect the three middle points to make the
        // upside-down triangle in the middle.
        // ------------------------------------------------------------
        drawTriangleOutline(
            turtle,
            leftTopMid,
            rightTopMid,
            bottomMid,
            CUT_GUIDE_COLOR,
            2
        );

        // ------------------------------------------------------------
        // Step 4: Remove or leave blank that central triangle.
        //
        // This fills the central triangle with the background color.
        // ------------------------------------------------------------
        fillTriangle(
            turtle,
            leftTopMid,
            rightTopMid,
            bottomMid,
            REMOVED_COLOR,
            REMOVED_COLOR,
            1
        );

        // ------------------------------------------------------------
        // Step 5: Repeat the exact same process on the three remaining
        // upright triangles.
        // ------------------------------------------------------------

        // Lower-left upright triangle
        constructSierpinski(
            turtle,
            left,
            bottomMid,
            leftTopMid,
            depth - 1
        );

        // Lower-right upright triangle
        constructSierpinski(
            turtle,
            bottomMid,
            right,
            rightTopMid,
            depth - 1
        );

        // Upper upright triangle
        constructSierpinski(
            turtle,
            leftTopMid,
            rightTopMid,
            top,
            depth - 1
        );
    }

    /**
     * Draws the outline of a triangle.
     */
    private static void drawTriangleOutline(
        Turtle turtle,
        Vector2D first,
        Vector2D second,
        Vector2D third,
        Color outlineColor,
        double penWidth
    ) {
        turtle.penUp();
        turtle.goTo(first.getX(), first.getY());

        turtle.penDown();
        turtle.penColor(outlineColor);
        turtle.penWidth(penWidth);

        turtle.goTo(second.getX(), second.getY());
        turtle.goTo(third.getX(), third.getY());
        turtle.goTo(first.getX(), first.getY());
    }

    /**
     * Draws and fills a triangle.
     */
    private static void fillTriangle(
        Turtle turtle,
        Vector2D first,
        Vector2D second,
        Vector2D third,
        Color outlineColor,
        Color fillColor,
        double penWidth
    ) {
        turtle.penUp();
        turtle.goTo(first.getX(), first.getY());

        turtle.penDown();
        turtle.penColor(outlineColor);
        turtle.penWidth(penWidth);
        turtle.fillColor(fillColor);

        turtle.beginFill();
        turtle.goTo(second.getX(), second.getY());
        turtle.goTo(third.getX(), third.getY());
        turtle.goTo(first.getX(), first.getY());
        turtle.endFill();
    }

    /**
     * Returns the midpoint of two points.
     */
    private static Vector2D midpoint(
        Vector2D first,
        Vector2D second
    ) {
        double x = (first.getX() + second.getX()) / 2.0;
        double y = (first.getY() + second.getY()) / 2.0;
        return new Vector2D(x, y);
    }
}