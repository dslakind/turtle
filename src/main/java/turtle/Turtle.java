package turtle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Turtle {
    private Vector2D position;
    private double heading;
    private Pen pen;
    private List<LineSegment> segments;

    public Turtle() {
        position = new Vector2D(0, 0);
        heading = 0;
        pen = new Pen();
        segments = new ArrayList<>();
    }

    public void forward(double distance) {
        if (distance == 0) return;
        double radians = degreesToRadians(heading);
        Vector2D newDestination = position.add(
            new Vector2D(distance * Math.cos(radians), distance * Math.sin(radians))
        );
        goTo(newDestination);
    }

    public void backward(double distance) {
        forward(-distance);
    }

    public void left(double angle) {
        heading += angle;
        heading = ((heading % 360) + 360) % 360;
    }

    public void right(double angle) {
        left(-angle);
    }

    public void goTo(double x, double y) {
        goTo(new Vector2D(x, y));
    }

    private void goTo(Vector2D destination) {
        if (pen.isDown()) {
            segments.add(
                new LineSegment(
                    position,
                    destination,
                    pen.getColor(),
                    pen.getWidth() 
                )
            );
        }
        position = destination;
    }

    public void home() {
        goTo(0,0);
    }

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public Vector2D getPosition() {
        return position;
    }

    public double getHeading() {
        return heading;
    }

    public Pen getPen() {
        return pen;
    }

    public void penUp() { pen.penUp(); }

    public void penDown() { pen.penDown(); }

    public List<LineSegment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public void setHeading(double angle) {
        heading = angle;
    }
}