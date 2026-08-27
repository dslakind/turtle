package turtle.demo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import turtle.Turtle;
import turtle.TurtleCanvas;
import turtle.Vector2D;

/**
 * Animated Sierpinski Triangle demo.
 *
 * This version is tuned so you can watch the fractal being built step by step.
 * Use a smaller recursion depth and a nonzero speed so the animation is visible.
 */
public class SierpinskiTriangleDemo {

    /*
     * Use a smaller depth for visible animation.
     *
     * depth 0 -> 1 triangle
     * depth 1 -> 3 triangles
     * depth 2 -> 9 triangles
     * depth 3 -> 27 triangles
     * depth 4 -> 81 triangles
     *
     * For animation, 3 is a good starting point.
     */
    private static final int RECURSION_DEPTH = 3;

    /*
     * Animation speed:
     * 1 = slow
     * 10 = fast
     * 0 = no animation
     */
    private static final int ANIMATION_SPEED = 6;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Create the turtle model.
            Turtle turtle = new Turtle();

            /*
             * Create the canvas before issuing drawing commands so the
             * recorded movements are animated on screen.
             */
            TurtleCanvas canvas = new TurtleCanvas(turtle);

            // Create the Swing window.
            JFrame frame = new JFrame("Animated Sierpinski Triangle");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Configure visible animation.
            turtle.speed(ANIMATION_SPEED);

            // Configure appearance.
            turtle.penColor(Color.BLACK);
            turtle.penWidth(1);
            turtle.fillColor(new Color(50, 120, 220));

            /*
             * Define the outer equilateral triangle.
             */
            Vector2D left = new Vector2D(-250, -170);
            Vector2D right = new Vector2D(250, -170);
            Vector2D top = new Vector2D(0, 263);

            // Draw the fractal.
            drawSierpinski(turtle, left, right, top, RECURSION_DEPTH);

            // Return the turtle to the center without drawing.
            turtle.penUp();
            turtle.home();
            turtle.setHeading(90);
        });
    }

    /**
     * Recursively draws a Sierpinski Triangle.
     */
    private static void drawSierpinski(
        Turtle turtle,
        Vector2D left,
        Vector2D right,
        Vector2D top,
        int depth
    ) {
        // Base case: draw one filled triangle.
        if (depth == 0) {
            drawFilledTriangle(turtle, left, right, top);
            return;
        }

        // Compute side midpoints.
        Vector2D bottomMidpoint = midpoint(left, right);
        Vector2D leftMidpoint = midpoint(left, top);
        Vector2D rightMidpoint = midpoint(right, top);

        // Lower-left triangle.
        drawSierpinski(
            turtle,
            left,
            bottomMidpoint,
            leftMidpoint,
            depth - 1
        );

        // Lower-right triangle.
        drawSierpinski(
            turtle,
            bottomMidpoint,
            right,
            rightMidpoint,
            depth - 1
        );

        // Upper triangle.
        drawSierpinski(
            turtle,
            leftMidpoint,
            rightMidpoint,
            top,
            depth - 1
        );
    }

    /**
     * Draws and fills one triangle.
     */
    private static void drawFilledTriangle(
        Turtle turtle,
        Vector2D first,
        Vector2D second,
        Vector2D third
    ) {
        // Move to the first vertex without drawing.
        turtle.penUp();
        turtle.goTo(first.getX(), first.getY());

        // Draw the triangle.
        turtle.penDown();
        turtle.beginFill();

        turtle.goTo(second.getX(), second.getY());
        turtle.goTo(third.getX(), third.getY());
        turtle.goTo(first.getX(), first.getY());

        turtle.endFill();
    }

    /**
     * Returns the midpoint between two points.
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