package turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TestContracts {

    @Test
    void penConstructorRejectsNonPositiveWidth() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Pen(true, Color.BLACK, 0)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new Pen(true, Color.BLACK, -1)
        );
    }

    @Test
    void penCopyIsIndependentOfLaterChanges() {
        Pen original = new Pen(true, Color.RED, 2);
        Pen copy = new Pen(original);

        original.penUp();
        original.setColor(Color.BLUE);
        original.setWidth(5);

        assertEquals(new Pen(true, Color.RED, 2), copy);
    }

    @Test
    void movementReturnsDefensivePositionCopies() {
        Vector2D from = new Vector2D(1, 2);
        Vector2D to = new Vector2D(3, 4);
        Movement movement = new Movement(from, to, false, 90);

        assertEquals(from, movement.getFrom());
        assertEquals(to, movement.getTo());
        assertNotSame(from, movement.getFrom());
        assertNotSame(to, movement.getTo());
        assertNotSame(movement.getFrom(), movement.getFrom());
        assertNotSame(movement.getTo(), movement.getTo());
    }

    @Test
    void filledPolygonCopiesInputAndProtectsReturnedPoints() {
        Vector2D firstPoint = new Vector2D(0, 0);
        List<Vector2D> sourcePoints = new ArrayList<>(
            List.of(
                firstPoint,
                new Vector2D(10, 0),
                new Vector2D(0, 10)
            )
        );
        FilledPolygon polygon = new FilledPolygon(
            sourcePoints,
            Color.CYAN,
            2
        );

        sourcePoints.clear();
        List<Vector2D> returnedPoints = polygon.getPoints();

        assertEquals(3, returnedPoints.size());
        assertEquals(firstPoint, returnedPoints.get(0));
        assertNotSame(firstPoint, returnedPoints.get(0));
        assertThrows(
            UnsupportedOperationException.class,
            () -> returnedPoints.add(new Vector2D(5, 5))
        );
    }

    @Test
    void turtleDrawingHistoriesAreUnmodifiable() {
        Turtle turtle = new Turtle();

        assertThrows(
            UnsupportedOperationException.class,
            () -> turtle.getSegments().clear()
        );
        assertThrows(
            UnsupportedOperationException.class,
            () -> turtle.getFilledPolygons().clear()
        );
    }

    @Test
    void turtleCanvasRejectsNullTurtle() {
        assertThrows(
            NullPointerException.class,
            () -> new TurtleCanvas(null)
        );
    }

    @Test
    void turtleCanvasUsesDocumentedDefaults() {
        Turtle turtle = new Turtle();
        turtle.penColor(Color.RED);

        TurtleCanvas canvas = new TurtleCanvas(turtle);

        assertSame(turtle, canvas.getTurtle());
        assertEquals(new Dimension(600, 600), canvas.getPreferredSize());
        assertEquals(Color.WHITE, canvas.getBackground());
        assertEquals(Color.RED, canvas.getForeground());
    }

    @Test
    void animationProgressRejectsInvalidMovementIndexes() {
        Turtle turtle = new Turtle();
        turtle.forward(10);
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        assertThrows(
            IllegalArgumentException.class,
            () -> canvas.setAnimationProgress(-1, 0)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> canvas.setAnimationProgress(2, 0)
        );
    }

    @Test
    void animationProgressRejectsInvalidFractions() {
        Turtle turtle = new Turtle();
        turtle.forward(10);
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        assertThrows(
            IllegalArgumentException.class,
            () -> canvas.setAnimationProgress(0, -0.01)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> canvas.setAnimationProgress(0, 1.01)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> canvas.setAnimationProgress(0, Double.NaN)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> canvas.setAnimationProgress(1, 0.5)
        );
    }

    @Test
    void screenRejectsNullTurtleBeforeCreatingSwingState() {
        assertThrows(
            NullPointerException.class,
            () -> new Screen(null)
        );
    }

    @Test
    void vectorValueContractIncludesCopyEqualityAndHashCode() {
        Vector2D vector = new Vector2D(1, 2);
        Vector2D copy = new Vector2D(vector);

        assertEquals(vector, copy);
        assertEquals(vector.hashCode(), copy.hashCode());
        assertNotSame(vector, copy);
        assertEquals("Vector2D [x=1.0, y=2.0]", vector.toString());
        org.junit.jupiter.api.Assertions.assertNotEquals(
            vector,
            new Vector2D(1, 3)
        );
        org.junit.jupiter.api.Assertions.assertNotEquals(vector, null);
        org.junit.jupiter.api.Assertions.assertNotEquals(vector, "not a vector");
        assertThrows(
            NullPointerException.class,
            () -> new Vector2D((Vector2D) null)
        );
    }

    @Test
    void filledPolygonValueAndValidationContracts() {
        List<Vector2D> points = List.of(
            new Vector2D(0, 0),
            new Vector2D(10, 0),
            new Vector2D(0, 10)
        );
        FilledPolygon polygon = new FilledPolygon(points, Color.BLUE, 2);
        FilledPolygon equalPolygon = new FilledPolygon(
            points,
            Color.BLUE,
            2
        );

        assertEquals(polygon, equalPolygon);
        assertEquals(polygon, polygon);
        org.junit.jupiter.api.Assertions.assertNotEquals(
            polygon,
            new FilledPolygon(
                List.of(
                    new Vector2D(0, 0),
                    new Vector2D(20, 0),
                    new Vector2D(0, 10)
                ),
                Color.BLUE,
                2
            )
        );

        org.junit.jupiter.api.Assertions.assertNotEquals(
            polygon,
            new FilledPolygon(
                List.of(
                    new Vector2D(0, 0),
                    new Vector2D(10, 0),
                    new Vector2D(0, 10)
                ),
                Color.BLUE,
                3
            )
        );

        assertEquals(polygon.hashCode(), equalPolygon.hashCode());
        assertEquals(
            "FilledPolygon [points=" + points + ", color=" + Color.BLUE  + ", completionMovementIndex=" + 2 + "]",
            polygon.toString()
        );
        org.junit.jupiter.api.Assertions.assertNotEquals(
            polygon,
            new FilledPolygon(points, Color.RED, 2)
        );
        org.junit.jupiter.api.Assertions.assertNotEquals(polygon, null);
        org.junit.jupiter.api.Assertions.assertNotEquals(
            polygon,
            "not a polygon"
        );

        List<Vector2D> pointsWithNull = new ArrayList<>();
        pointsWithNull.add(null);

        assertThrows(
            NullPointerException.class,
            () -> new FilledPolygon(null, Color.BLUE, 0)
        );
        assertThrows(
            NullPointerException.class,
            () -> new FilledPolygon(pointsWithNull, Color.BLUE, 0)
        );
        assertThrows(
            NullPointerException.class,
            () -> new FilledPolygon(points, null, 0)
        );
    }

    @Test
    void penRejectsNullColorsAndNonFiniteWidths() {
        assertThrows(
            NullPointerException.class,
            () -> new Pen(true, null, 1)
        );

        Pen pen = new Pen();

        assertThrows(
            NullPointerException.class,
            () -> pen.setColor(null)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new Pen(true, Color.BLACK, Double.NaN)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> pen.setWidth(Double.POSITIVE_INFINITY)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> pen.setWidth(Double.NEGATIVE_INFINITY)
        );
    }

    @Test
    void lineSegmentRejectsInvalidCopyAndNonFiniteWidths() {
        Vector2D from = new Vector2D(0, 0);
        Vector2D to = new Vector2D(1, 1);

        assertThrows(
            NullPointerException.class,
            () -> new LineSegment((LineSegment) null)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new LineSegment(from, to, Color.BLACK, Double.NaN)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new LineSegment(
                from,
                to,
                Color.BLACK,
                Double.POSITIVE_INFINITY
            )
        );
    }

    @Test
    void movementHasCompleteValueAndValidationContracts() {
        Movement movement = new Movement(
            new Vector2D(0, 0),
            new Vector2D(10, 5),
            true,
            45
        );
        Movement equalMovement = new Movement(
            new Vector2D(0, 0),
            new Vector2D(10, 5),
            true,
            45
        );
        Movement thirdEqualMovement = new Movement(
            new Vector2D(0, 0),
            new Vector2D(10, 5),
            true,
            45
        );

        assertEquals(movement, movement);
        assertEquals(movement, equalMovement);
        assertEquals(equalMovement, movement);
        assertEquals(equalMovement, thirdEqualMovement);
        assertEquals(movement, thirdEqualMovement);
        assertEquals(movement.hashCode(), equalMovement.hashCode());

        assertNotEquals(
            movement,
            new Movement(new Vector2D(1, 0), new Vector2D(10, 5), true, 45)
        );
        assertNotEquals(
            movement,
            new Movement(new Vector2D(0, 0), new Vector2D(11, 5), true, 45)
        );
        assertNotEquals(
            movement,
            new Movement(new Vector2D(0, 0), new Vector2D(10, 5), false, 45)
        );
        assertNotEquals(
            movement,
            new Movement(new Vector2D(0, 0), new Vector2D(10, 5), true, 90)
        );
        assertNotEquals(movement, null);
        assertNotEquals(movement, "not a movement");

        assertThrows(
            NullPointerException.class,
            () -> new Movement(null, new Vector2D(1, 1), true, 0)
        );
        assertThrows(
            NullPointerException.class,
            () -> new Movement(new Vector2D(0, 0), null, true, 0)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new Movement(
                new Vector2D(0, 0),
                new Vector2D(1, 1),
                true,
                Double.NaN
            )
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new Movement(
                new Vector2D(0, 0),
                new Vector2D(1, 1),
                true,
                Double.POSITIVE_INFINITY
            )
        );
    }

    @Test
    void vectorEqualityAndNullContractsCoverDoubleEdgeCases() {
        Vector2D positiveZero = new Vector2D(0.0, 0.0);
        Vector2D negativeZero = new Vector2D(-0.0, 0.0);
        Vector2D firstNaN = new Vector2D(Double.NaN, 1);
        Vector2D secondNaN = new Vector2D(Double.NaN, 1);

        assertNotEquals(positiveZero, negativeZero);
        assertEquals(firstNaN, secondNaN);
        assertEquals(firstNaN.hashCode(), secondNaN.hashCode());
        assertThrows(NullPointerException.class, () -> positiveZero.add(null));
        assertThrows(
            NullPointerException.class,
            () -> positiveZero.distanceTo(null)
        );
    }

    @Test
    void turtleHistoryGettersRemainUnmodifiableLiveViews() {
        Turtle turtle = new Turtle();
        List<LineSegment> segments = turtle.getSegments();
        List<FilledPolygon> polygons = turtle.getFilledPolygons();

        turtle.forward(10);

        assertEquals(1, segments.size());
        assertThrows(UnsupportedOperationException.class, segments::clear);

        turtle.beginFill();
        turtle.forward(10);
        turtle.left(120);
        turtle.forward(10);
        turtle.left(120);
        turtle.forward(10);
        turtle.endFill();

        assertEquals(1, polygons.size());
        assertThrows(UnsupportedOperationException.class, polygons::clear);
    }
}