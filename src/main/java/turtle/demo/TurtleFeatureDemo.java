package turtle.demo;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import turtle.Turtle;
import turtle.TurtleCanvas;

/**
 * Manual demonstration of the major Turtle features implemented so far.
 *
 * Demonstrates:
 * - Swing animation
 * - Turtle cursor position and heading
 * - speed()
 * - forward()
 * - backward()
 * - left()
 * - right()
 * - setHeading()
 * - goTo()
 * - home()
 * - penUp() / penDown()
 * - penColor()
 * - penWidth()
 * - fillColor()
 * - beginFill() / endFill()
 *
 * The Turtle model records commands immediately. TurtleCanvas then
 * animates through the recorded movements in order.
 */
public class TurtleFeatureDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // ------------------------------------------------------------
            // Create the turtle model.
            // ------------------------------------------------------------
            Turtle turtle = new Turtle();

            /*
             * IMPORTANT:
             * Create the canvas BEFORE issuing the movement commands.
             *
             * Movements that already exist when TurtleCanvas is constructed
             * are treated as movements that have already been displayed.
             */
            TurtleCanvas canvas = new TurtleCanvas(turtle);

            // ------------------------------------------------------------
            // Create and display the Swing window.
            // ------------------------------------------------------------
            JFrame frame = new JFrame("Turtle Feature Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ------------------------------------------------------------
            // Animation speed
            // ------------------------------------------------------------

            /*
             * Speed 0 is immediate.
             * Speeds 1 through 10 animate from slower to faster.
             *
             * Keep one speed for this demo because the current animation
             * system uses the turtle's current speed while replaying
             * pending movements.
             */
            turtle.speed(3);

            // ============================================================
            // 1. BLUE SQUARE
            //
            // Demonstrates:
            // - pen color
            // - pen width
            // - forward movement
            // - left turns
            // ============================================================

            turtle.penColor(Color.BLUE);
            turtle.penWidth(3);
            turtle.setHeading(0);

            for (int i = 0; i < 4; i++) {
                turtle.forward(100);
                turtle.left(90);
            }

            // ============================================================
            // 2. PEN-UP MOVEMENT
            //
            // The turtle should visibly move to the new location without
            // leaving a line behind it.
            // ============================================================

            turtle.penUp();
            turtle.goTo(-180, 130);

            // Point right before beginning the next shape.
            turtle.setHeading(0);

            // Resume drawing.
            turtle.penDown();

            // ============================================================
            // 3. FILLED TRIANGLE
            //
            // Demonstrates:
            // - separate pen and fill colors
            // - beginFill()
            // - endFill()
            //
            // The red outline should animate first. The yellow fill should
            // remain hidden until all movements belonging to the polygon
            // have finished visually.
            // ============================================================

            turtle.penColor(Color.RED);
            turtle.penWidth(2);
            turtle.fillColor(Color.YELLOW);

            turtle.beginFill();

            turtle.forward(100);
            turtle.left(120);

            turtle.forward(100);
            turtle.left(120);

            turtle.forward(100);

            turtle.endFill();

            // ============================================================
            // 4. MOVE WITHOUT DRAWING
            //
            // Again demonstrates animated pen-up movement.
            // ============================================================

            turtle.penUp();
            turtle.goTo(120, 130);

            turtle.setHeading(0);
            turtle.penDown();

            // ============================================================
            // 5. MAGENTA STAR
            //
            // Demonstrates repeated movement and right turns.
            // ============================================================

            turtle.penColor(Color.MAGENTA);
            turtle.penWidth(2);

            for (int i = 0; i < 5; i++) {
                turtle.forward(100);
                turtle.right(144);
            }

            // ============================================================
            // 6. MOVE TO LOWER PART OF THE CANVAS
            // ============================================================

            turtle.penUp();
            turtle.goTo(-150, -140);

            turtle.setHeading(0);
            turtle.penDown();

            // ============================================================
            // 7. GREEN FORWARD / BACKWARD EXAMPLE
            //
            // Demonstrates backward() as well as forward().
            // ============================================================

            turtle.penColor(Color.GREEN);
            turtle.penWidth(4);

            turtle.forward(100);

            /*
             * backward() moves opposite the current heading without
             * changing that heading.
             */
            turtle.backward(50);

            // Turn upward and draw another segment.
            turtle.left(90);
            turtle.forward(75);

            // ============================================================
            // 8. THINNER ORANGE PATH
            //
            // Demonstrates changing pen width and color between movements.
            // ============================================================

            turtle.penColor(Color.ORANGE);
            turtle.penWidth(1);

            turtle.right(90);
            turtle.forward(60);

            turtle.left(90);
            turtle.forward(50);

            // ============================================================
            // 9. RETURN HOME WITHOUT DRAWING
            //
            // home() returns the turtle to (0, 0).
            // With the pen up, the turtle should visibly travel home
            // without leaving another line.
            // ============================================================

            turtle.penUp();
            turtle.home();

            // ============================================================
            // 10. FINAL HEADING
            //
            // Turns currently change the visible cursor orientation when
            // the next movement begins; the turn itself is not animated.
            // ============================================================

            turtle.setHeading(90);

            /*
             * Make one final pen-up movement so the heading-aware turtle
             * cursor can be seen pointing upward.
             */
            turtle.forward(30);
        });
    }
}