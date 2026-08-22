package turtle;

import java.util.Objects;

import javax.swing.JFrame;

/** A Swing window that displays a live {@link Turtle} through a {@link TurtleCanvas}. */
public class Screen{
    private final Turtle turtle;
    private final TurtleCanvas canvas;
    private final JFrame frame;

    /**
     * Creates a screen for {@code initTurtle} with an 800 by 600 window.
     *
     * @param initTurtle turtle whose recorded segments are rendered
     * @throws NullPointerException if {@code initTurtle} is null
     */
    public Screen(Turtle initTurtle) {
        Objects.requireNonNull(initTurtle, "initTurtle cannot be null");
        turtle = initTurtle;
        frame = new JFrame();

        canvas = new TurtleCanvas(turtle);
        frame.setTitle("Turtle Graphics");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(canvas);
    }

    /** Makes the screen window visible. */
    public void show() {
        frame.setVisible(true);
    }

    Turtle getTurtle() {
        return turtle;
    }

    TurtleCanvas getCanvas() {
        return canvas;
    }

    JFrame getFrame() {
        return frame;
    }
}
