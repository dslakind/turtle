package turtle;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Objects;

import javax.swing.JPanel;

/** Swing canvas that renders the recorded segments of a live {@link Turtle}. */
public class TurtleCanvas extends JPanel {
    private final Turtle turtle;

    /**
     * Creates a canvas for {@code initTurtle} with a white background and a
     * preferred size of 600 by 600 pixels.
     *
     * @param initTurtle turtle whose segments are rendered
     * @throws NullPointerException if {@code initTurtle} is null
     */
    public TurtleCanvas(Turtle initTurtle) {
        turtle = Objects.requireNonNull(initTurtle);

        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
        setForeground(turtle.getPen().getColor());
    }

    Turtle getTurtle() {
        return turtle;
    }

    /** Paints each recorded segment using its stored color and width. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        try {
            g2d.setColor(getForeground());

            List<LineSegment> segments = turtle.getSegments();

            for (LineSegment segment : segments) {
                Vector2D from = segment.getFrom();
                Vector2D to = segment.getTo();

                int x1 = toScreenX(from.getX());
                int y1 = toScreenY(from.getY());
                int x2 = toScreenX(to.getX());
                int y2 = toScreenY(to.getY());

                g2d.setColor(segment.getColor());
                g2d.setStroke(
                    new BasicStroke((float) segment.getWidth())
                );

                g2d.drawLine(x1, y1, x2, y2);
            }
        } finally {
            g2d.dispose();
        }
    }

    // convert x to screen-x
    private int toScreenX(double turtleX) {
        return (int) Math.round(getWidth() / 2.0 + turtleX);
    }

    // convert y to screen-y
    private int toScreenY(double turtleY) {
        return (int) Math.round(getHeight() / 2.0 - turtleY);
    }

}