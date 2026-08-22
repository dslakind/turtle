package turtle;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

public class TestTurtleCanvas {
    @Test
    void isAJPanel() {
        Turtle turtle = new Turtle();

        TurtleCanvas canvas = new TurtleCanvas(turtle);

        assertInstanceOf(JPanel.class, canvas);
    }

    @Test
    void retainsSameTurtle() {
        Turtle turtle = new Turtle();

        TurtleCanvas canvas = new TurtleCanvas(turtle);

        assertSame(turtle, canvas.getTurtle());
    }

}
