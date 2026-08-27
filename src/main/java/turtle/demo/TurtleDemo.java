package turtle.demo;

import java.awt.Color;
import javax.swing.SwingUtilities;

import turtle.Screen;
import turtle.Turtle;

/** Small manual demo showing filled shapes and their outlines. */
public class TurtleDemo {

    /** Launches the Swing demo on the event-dispatch thread. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Turtle turtle = new Turtle();
            turtle.speed(1);

            // Move from the origin to the starting position for the square.
            moveWithoutDrawing(turtle, -250, 0);

            turtle.getPen().setColor(Color.BLUE);
            turtle.getPen().setWidth(3);
            turtle.fillColor(Color.CYAN);
            turtle.beginFill();
            drawSquare(turtle, 100);
            turtle.endFill();

            // Move to the triangle.
            moveWithoutDrawing(turtle, 180, 0);

            turtle.getPen().setColor(Color.PINK);
            turtle.getPen().setWidth(3);
            turtle.fillColor(Color.GREEN);
            turtle.beginFill();
            drawTriangle(turtle, 120);
            turtle.endFill();

            // Move to the star.
            moveWithoutDrawing(turtle, 200, 50);

            turtle.getPen().setColor(Color.MAGENTA);
            turtle.getPen().setWidth(3);
            turtle.fillColor(Color.PINK);
            turtle.beginFill();
            drawStar(turtle, 120);
            turtle.endFill();

            Screen screen = new Screen(turtle);
            screen.show();
        });
    }

    private static void drawSquare(Turtle turtle, double sideLength) {
        for (int i = 0; i < 4; i++) {
            turtle.forward(sideLength);
            turtle.left(90);
        }
    }

    private static void drawTriangle(Turtle turtle, double sideLength) {
        for (int i = 0; i < 3; i++) {
            turtle.forward(sideLength);
            turtle.left(120);
        }
    }

    private static void drawStar(Turtle turtle, double sideLength) {
        for (int i = 0; i < 5; i++) {
            turtle.forward(sideLength);
            turtle.left(144);
        }
    }

    /**
     * Draws a flower made from thirty rotated five-point stars.
     *
     * @param turtle turtle to move and draw with
     * @param sideLength length of each star edge
     * @throws NullPointerException if {@code turtle} is null
     */
    public static void drawFlower(Turtle turtle, double sideLength) {
        for (int pedal = 0; pedal < 360/10; pedal++) {
            int r = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            Color color = new Color(r, g, b);
            turtle.getPen().setColor(color);
            drawStar(turtle, sideLength);
            turtle.left(36);
        }


    }

    private static void moveWithoutDrawing(
            Turtle turtle, double xDistance, double yDistance) {

        turtle.getPen().penUp();

        // Move vertically while ultimately preserving an east-facing heading.
        if (yDistance > 0) {
            turtle.left(90);
            turtle.forward(yDistance);
            turtle.right(90);
        } else if (yDistance < 0) {
            turtle.right(90);
            turtle.forward(-yDistance);
            turtle.left(90);
        }

        // Move horizontally while ultimately preserving an east-facing heading.
        if (xDistance >= 0) {
            turtle.forward(xDistance);
        } else {
            turtle.left(180);
            turtle.forward(-xDistance);
            turtle.left(180);
        }

        turtle.getPen().penDown();
    }
}