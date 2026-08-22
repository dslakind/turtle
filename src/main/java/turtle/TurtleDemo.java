package turtle;

import java.awt.Color;
import javax.swing.SwingUtilities;

public class TurtleDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Turtle turtle = new Turtle();

            // Move from the origin to the starting position for the square.
            moveWithoutDrawing(turtle, -250, 0);

            turtle.getPen().setColor(Color.RED);
            turtle.getPen().setWidth(3);
            drawSquare(turtle, 100);

            // Move to the triangle.
            moveWithoutDrawing(turtle, 180, 0);

            turtle.getPen().setColor(Color.BLUE);
            turtle.getPen().setWidth(3);
            drawTriangle(turtle, 120);

            // Move to the star.
            moveWithoutDrawing(turtle, 300, 50);

            turtle.getPen().setColor(Color.MAGENTA);
            turtle.getPen().setWidth(3);
            drawFlower(turtle, 100);

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