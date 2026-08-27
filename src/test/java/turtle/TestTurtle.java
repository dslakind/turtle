package turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

// Use assertEquals(expected, actual, 1e-9) for all floating-point position checks.
class TestTurtle {
    /* Default state (Story 1.1) */
   @Test
    void testTurtleDefaultState() {
        Turtle turtle = new Turtle();
        // Position is (0, 0)
        assertEquals(new Vector2D(0, 0), turtle.getPosition());
        // Heading is 0
        assertEquals(0, turtle.getHeading(), 1e-9);
        assertTrue(turtle.getPen().isDown());
        // Pen is down, black, width 1
        assertEquals(Color.BLACK, turtle.getPen().getColor());
        assertEquals(1, turtle.getPen().getWidth(), 1e-9);
        // Segment list is empty
        assertEquals(0, turtle.getSegments().size());
        assertEquals(0, turtle.getMovements().size());
    }

    /* forward / backward (Story 1.2) */
    @Test
    void testForwardAndBackward() {
        Turtle turtle = new Turtle();
        // forward(100) at heading 0 → position (100, 0)
        turtle.forward(100);
        assertEquals(new Vector2D(100, 0), turtle.getPosition());
        // forward with pen down → one segment added with correct from, to, color, width
        List<LineSegment> segments = turtle.getSegments();
        LineSegment expectedLineSegment = new LineSegment(
            new Vector2D(0, 0),
            new Vector2D(100, 0),
            Color.BLACK,
            1
        );
        assertEquals(expectedLineSegment, segments.get(0));

        // Two consecutive forward calls → two segments, and from of second equals to of first
        turtle.forward(100);
        LineSegment expectedLineSegment2 = new LineSegment(
            new Vector2D(100, 0),
            new Vector2D(200, 0),
            Color.BLACK,
            1
        );
        assertEquals(expectedLineSegment2, segments.get(1));

        // forward and backward with pen up → no segment added
        turtle.penUp();
        turtle.forward(100);
        turtle.backward(200);
        turtle.penDown();
        assertEquals(2, segments.size());

        // forward(0) → position unchanged
        turtle.forward(0);
        assertEquals(new Vector2D(100, 0), turtle.getPosition());

        // backward(200) at heading 0 → position (-100, 0)
        turtle.backward(200);
        assertEquals(new Vector2D(-100, 0), turtle.getPosition());

        // forward(100) at heading 90 → position (0, 100)
        turtle.forward(100);
        turtle.setHeading(90);
        turtle.forward(100);
        assertEquals(0.0,   turtle.getPosition().getX(), 1e-9);
        assertEquals(100.0, turtle.getPosition().getY(), 1e-9);
    }

    /* right / left (Story 1.3) */
    @Test
    void testRightLeft() {
        Turtle yertle = new Turtle();
        // right(90) from 0 → heading 270 (or -90 if not normalizing yet)
        yertle.right(90);
        assertEquals(270, yertle.getHeading());
        yertle.left(90);
        // left(90) from 0 → heading 90
        yertle.left(90);
        assertEquals(90, yertle.getHeading());
        yertle.right(90);
        // right(90) then left(90) → back to original heading
        yertle.right(90);
        yertle.left(90);
        assertEquals(0, yertle.getHeading());
        // right(360) → heading unchanged (0)
        yertle.right(360);
        assertEquals(0, yertle.getHeading());
        // right(angle) with angle > 360 wraps correctly
        yertle.right(360 + 45);
        assertEquals(360-45, yertle.getHeading(), 1e-9);
        // penUp / penDown (Story 1.4)
        yertle.penUp();
        assertEquals(false, yertle.getPen().isDown());
        // penUp() then forward → no segment
        yertle.forward(100);
        assertEquals(0, yertle.getSegments().size());
        assertEquals(1, yertle.getMovements().size());

        yertle.backward(100);
        // penUp() then penDown() then forward → one segment
        yertle.penDown();
        yertle.forward(100);
        assertEquals(1, yertle.getSegments().size());
        assertEquals(3, yertle.getMovements().size());
    }

    // goTo / setHeading (Story 1.5)
    @Test
    void testGoTo() {
        Turtle yertle = new Turtle();
        yertle.setHeading(90);
        // goTo(5, 3) → position is (5, 3) regardless of heading
        yertle.goTo(5, 3);
        assertEquals(5, yertle.getPosition().getX(), 1e-9);
        assertEquals(3, yertle.getPosition().getY(), 1e-9);
        // goTo with pen down → segment recorded from old position to new
        Vector2D from = new Vector2D(0, 0);
        Vector2D to = new Vector2D(5, 3);
        LineSegment segment = new LineSegment(from, to, Color.BLACK, 1);
        assertEquals(segment, yertle.getSegments().get(0));
        // goTo with pen up → no segment
        yertle.penUp();
        assertEquals(1, yertle.getSegments().size());
        assertEquals(1, yertle.getMovements().size());
    }

    /* Segment history (Story 1.6) */
    @Test
    void testSegmentHistory() {
        Turtle yertle = new Turtle();
        // Three forward calls with pen down → three segments in order
        yertle.forward(50);
        Vector2D from = new Vector2D(0, 0);
        Vector2D to = new Vector2D(50, 0);
        LineSegment segment = new LineSegment(from, to, Color.BLACK, 1);
        assertEquals(segment, yertle.getSegments().get(0));

        yertle.forward(50);
        from = to;
        to = new Vector2D(100, 0);
        segment = new LineSegment(from, to, Color.BLACK, 1);
        assertEquals(segment, yertle.getSegments().get(1));

        yertle.forward(50);
        from = to;
        to = new Vector2D(150, 0);
        segment = new LineSegment(from, to, Color.BLACK, 1);
        assertEquals(segment, yertle.getSegments().get(2));

        // penUp, forward, penDown, forward → only one segment (the second move)
        yertle.penUp();
        yertle.forward(100);
        yertle.penDown();
        yertle.forward(100);
        assertEquals(4, yertle.getSegments().size());
        assertEquals(5, yertle.getMovements().size());
    }

    /* home (Story 1.7) */
    @Test
    void testHome() {
        Turtle yertle = new Turtle();
        // home() → position is (0, 0)
        yertle.penUp();
        yertle.forward(100);
        yertle.home();
        assertEquals(0, yertle.getPosition().getX(), 1e-9);
        assertEquals(0, yertle.getPosition().getY(), 1e-9);
        // home() with pen up → no segment
        assertEquals(0, yertle.getSegments().size());
        assertEquals(2, yertle.getMovements().size());
        // home() does not change heading
        assertEquals(0, yertle.getHeading(),1e-9);
        // home() with pen down → segment recorded from current position to origin
        yertle.forward(100);
        yertle.penDown();
        yertle.home();
        Vector2D from = new Vector2D(100, 0);
        Vector2D to = new Vector2D(0, 0);
        LineSegment segment = new LineSegment(from, to, Color.BLACK, 1);
        assertEquals(segment, yertle.getSegments().get(0));
    }

    /* Story 3.1: penColor(Color) on Turtle. The Pen already owns color state;
       Turtle exposes the convenience forwarding method. */
    @Test
    void penColorChangesCurrentPenColor() {
        Turtle yertle = new Turtle();
        yertle.penColor(Color.RED);
        assertEquals(Color.RED, yertle.getPen().getColor());
    }

    @Test
    void movementRecordsCurrentPenColor() {
        Turtle yertle = new Turtle();
        yertle.penColor(Color.RED);
        yertle.forward(100);
        assertEquals(1, yertle.getSegments().size());
        assertEquals(1, yertle.getMovements().size());
        LineSegment segment = yertle.getSegments().get(0);
        assertEquals(Color.RED, segment.getColor());
    }

    @Test
    void changingPenColorAffectsSubsequentSegments() {
        Turtle yertle = new Turtle();
        yertle.penColor(Color.RED);
        yertle.forward(100);

        yertle.penColor(Color.BLUE);
        yertle.forward(100);
        assertEquals(2, yertle.getSegments().size());
        assertEquals(2, yertle.getMovements().size());
        LineSegment segment1 = yertle.getSegments().get(0);
        assertEquals(Color.RED, segment1.getColor());

        LineSegment segment2 = yertle.getSegments().get(1);
        assertEquals(Color.BLUE, segment2.getColor());
    }

    /* Story 3.2: penWidth(width) on Turtle. The Pen already owns width state;
       Turtle exposes the convenience forwarding method. */
    @Test
    void penWidthChangesCurrentPenWidth() {
        Turtle yertle = new Turtle();
        yertle.penWidth(5);
        assertEquals(5, yertle.getPen().getWidth());
    }

    @Test
    void movementRecordsCurrentPenWidth() {
        Turtle yertle = new Turtle();
        yertle.penWidth(5);
        yertle.forward(100);
        assertEquals(1, yertle.getSegments().size());
        assertEquals(1, yertle.getMovements().size());
        LineSegment segment = yertle.getSegments().get(0);
        assertEquals(5, segment.getWidth());
    }

    @Test
    void changingPenWidthAffectsSubsequentSegments() {
        Turtle yertle = new Turtle();
        yertle.penWidth(2);
        yertle.forward(100);

        yertle.penWidth(5);
        yertle.forward(100);
        assertEquals(2, yertle.getSegments().size());
        assertEquals(2, yertle.getMovements().size());
        LineSegment segment1 = yertle.getSegments().get(0);
        assertEquals(2, segment1.getWidth());

        LineSegment segment2 = yertle.getSegments().get(1);
        assertEquals(5, segment2.getWidth());
    }

    @Test
    void penWidth_zeroOrNegative_throws() {
        Turtle yertle = new Turtle();
        assertThrows(IllegalArgumentException.class, () -> yertle.penWidth(0));
        assertThrows(IllegalArgumentException.class, () -> yertle.penWidth(-1));
    }

    /* Story 4.1 — fillColor(color) stores a fill color separately from the pen color. */

    // A new turtle starts with fill color Color.BLACK.
    @Test
    void turtleStartsWithBlackFillColor() {
        Turtle yertle = new Turtle();
        assertEquals(Color.BLACK, yertle.getFillColor());
    }

    // fillColor(Color.RED) updates the fill color.
    @Test
    void fillColorUpdatesCorrectly() {
        Turtle yertle = new Turtle();
        assertEquals(Color.BLACK, yertle.getFillColor());
        yertle.fillColor(Color.RED);
        assertEquals(Color.RED, yertle.getFillColor());
    }

    // Setting fill color does not change penColor.
    @Test
    void fillColorDoesNotChangePenColor() {
        Turtle yertle = new Turtle();
        yertle.fillColor(Color.RED);
        assertEquals(Color.RED, yertle.getFillColor());
        assertEquals(Color.BLACK, yertle.getPen().getColor());
    }

    // Setting pen color does not change fill color.
    @Test
    void changingPenColorDoesNotChangeFillColor() {
        Turtle yertle = new Turtle();
        yertle.getPen().setColor(Color.RED);
        assertEquals(Color.BLACK, yertle.getFillColor());
        assertEquals(Color.RED, yertle.getPen().getColor());
    }

    // Passing null throws NullPointerException.
    @Test
    void nullFillColorThrowsException() {
        Turtle yertle = new Turtle();
        assertThrows(NullPointerException.class, () -> yertle.fillColor(null));
    }

    /* Story 4.2 — beginFill() starts recording a polygon path from the current turtle position. */

    // beginFill() marks the turtle as actively recording a fill.
    @Test
    void beginFillStartsFilling() {
        Turtle yertle = new Turtle();
        assertFalse(yertle.isFilling());
        yertle.beginFill();
        assertTrue(yertle.isFilling());
    }

    @Test
    void beginFillStartsAtCurrentPosition() {
        Turtle yertle = new Turtle();
        yertle.beginFill();
        assertEquals(yertle.getPosition(), yertle.getstartFillPosition());
        assertEquals(1, yertle.getFillPath().size());
        assertEquals(yertle.getPosition(), yertle.getFillPath().get(0));
    }

    @Test
    void beginFillDoesNotMoveTurtle() {
        Turtle yertle = new Turtle();
        yertle.beginFill();
        Vector2D pos = new Vector2D(0, 0);
        assertEquals(pos, yertle.getPosition());
    }

    @Test
    void beginFillDoesNotCreateSegment() {
        Turtle yertle = new Turtle();
        yertle.beginFill();
        assertEquals(0, yertle.getSegments().size());
        assertEquals(0, yertle.getMovements().size());
    }

    @Test
    void beginFillMovementCreatesPath() {
        Turtle yertle = new Turtle();
        yertle.goTo(10, 20);
        yertle.beginFill();
        yertle.goTo(30, 40);

        assertEquals(2, yertle.getFillPath().size());
        assertEquals(new Vector2D(10, 20), yertle.getFillPath().get(0));
        assertEquals(new Vector2D(30, 40), yertle.getFillPath().get(1));
    }

    @Test
    void beginFillPreservesHeadingAndPenState() {
        Turtle yertle = new Turtle();
        yertle.beginFill();
        assertEquals(0, yertle.getHeading());
        assertEquals(new Pen(), yertle.getPen());
    }

    @Test
    void beginFillRejectsNestedFill() {
        Turtle yertle = new Turtle();
        yertle.beginFill();
        assertThrows(IllegalStateException.class, () -> yertle.beginFill());
    }

     /* Story 4.3 — endFill() completes the path and publishes a filled polygon;
         filling does not render before completion.
    */
    private void drawFilledTriangle(Turtle turtle) {
        turtle.fillColor(Color.YELLOW);
        turtle.beginFill();
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.endFill();
    }
    @Test
    void triangleCreatesOneCompletedPolygon() {
        Turtle yertle = new Turtle();
        drawFilledTriangle(yertle);
        List<FilledPolygon> filledPolygons = yertle.getFilledPolygons();
        assertTrue(filledPolygons.size() == 1);
    }

    @Test
    void endFillPointsArePreservedInTheCorrectOrder() {
        Turtle yertle = new Turtle();
        ArrayList<Vector2D> vertices = new ArrayList<>();
        yertle.fillColor(Color.YELLOW);
        yertle.beginFill();
        vertices.add(yertle.getPosition());
        yertle.forward(100);
        vertices.add(yertle.getPosition());
        yertle.left(120);
        yertle.forward(100);
        vertices.add(yertle.getPosition());
        yertle.left(120);
        yertle.forward(100);
        vertices.add(yertle.getPosition());
        yertle.endFill();

        List<FilledPolygon> filledPolygons = yertle.getFilledPolygons();
        List<Vector2D> points = filledPolygons.get(0).getPoints();

        assertEquals(vertices.size(), points.size());
        for (int i = 0; i < points.size(); i++) {
            assertTrue(points.get(i).equals(vertices.get(i)));
        }
    }

    @Test
    void endFillCurrentFillColorIsCaptured() {
        Turtle yertle = new Turtle();

        yertle.fillColor(Color.RED);
        yertle.beginFill();
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.endFill();

        assertEquals(Color.RED, yertle.getFilledPolygons().get(0).getFillColor());
    }

    // Later fill-color changes do not affect earlier polygons.
    @Test
    void endFillLaterFillColorChangesDoNotAffectEarlierPolygons() {
       Turtle yertle = new Turtle();

        yertle.fillColor(Color.RED);
        yertle.beginFill();
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.endFill();

        assertEquals(Color.RED, yertle.getFilledPolygons().get(0).getFillColor());

        yertle.fillColor(Color.YELLOW);
        yertle.beginFill();
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.endFill();

        assertEquals(Color.YELLOW, yertle.getFilledPolygons().get(1).getFillColor());
    }

    // Filling state becomes false.
    @Test
    void endFillFillingStateBecomesFalse() {
       Turtle yertle = new Turtle();

        yertle.fillColor(Color.RED);
        yertle.beginFill();
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.endFill();

        assertFalse(yertle.isFilling());
    }

    // The active path is cleared.
    @Test
    void endFillActivePathIsCleared() {
       Turtle yertle = new Turtle();

        yertle.fillColor(Color.RED);
        yertle.beginFill();
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.endFill();

        assertEquals(0, yertle.getFillPath().size());
    }

    // An incomplete path creates no polygon.
    @Test
    void endFillIncompletePathCreatesNoPolygon() {
       Turtle yertle = new Turtle();

        yertle.fillColor(Color.RED);
        yertle.beginFill();
        yertle.forward(100);
        yertle.endFill();

        assertEquals(0, yertle.getFillPath().size());
        assertEquals(0, yertle.getFilledPolygons().size());
        assertFalse(yertle.isFilling());
    }

    // Calling endFill() while inactive does nothing.
    @Test
    void endfillCallWhileInactiveDoesNothing() {
       Turtle yertle = new Turtle();

        yertle.fillColor(Color.RED);
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.left(120);
        yertle.endFill();

        assertEquals(0, yertle.getFillPath().size());
        assertEquals(0, yertle.getFilledPolygons().size());
    }

    // endFill() does not alter turtle position, heading, or pen state.
    @Test
    void endFillDoesNotAlterTurtlePositionHeadingOrState() {
        Turtle yertle = new Turtle();

        yertle.fillColor(Color.YELLOW);
        yertle.beginFill();
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);
        yertle.left(120);
        yertle.forward(100);

        double headingBeforeEndFill = yertle.getHeading();
        Vector2D positionBeforeEndFill = yertle.getPosition();
        Pen penBeforeEndFill = new Pen(yertle.getPen());

        yertle.endFill();


        assertEquals(penBeforeEndFill, yertle.getPen());
        assertEquals(headingBeforeEndFill, yertle.getHeading());
        assertEquals(positionBeforeEndFill, yertle.getPosition());
    }

    // Story 5.1 — speed(level) setting (0 = instant, 1-10 = slow-to-fast).

    // A newly constructed Turtle has speed 3, matching Python turtle's default.
    @Test
    void turtleHasDefaultSpeed3() {
        Turtle yertle = new Turtle();
        assertEquals(3, yertle.getSpeed());
    }

    // speed(level) stores the turtle’s current speed.
    @Test
    void turtleRecordsChangedSpeed() {
        Turtle yertle = new Turtle();
        yertle.speed(7);
        assertEquals(7, yertle.getSpeed());
    }    

    // changing speed does not alter the turtle's position or heading.
    @Test
    void changingSpeedDoesNotAlterTurtlePositionOrHeading() {
        Turtle yertle = new Turtle();
        yertle.speed(7);
        assertEquals(0, yertle.getHeading());
        assertEquals(
            new Vector2D(0, 0), 
            yertle.getPosition()
        );
    }

    // changing speed does not change the pen state or recorded segments
    @Test
    void changingSpeedDoesNotChangePenStateOrRecordedSegments() {
        Turtle yertle = new Turtle();
        Pen pen = new Pen(yertle.getPen());
        yertle.forward(100);
        LineSegment segment = new LineSegment(yertle.getSegments().get(0));
        yertle.speed(7);
        assertEquals(1, yertle.getSegments().size());
        assertEquals(1, yertle.getMovements().size());
        assertEquals(pen, yertle.getPen());
        assertEquals(segment, yertle.getSegments().get(0));
    }

    // changing speed does not alter the active fill state.
    @Test
    void changingSpeedDoesNotAlterActiveFillState() {
        Turtle yertle = new Turtle();
        yertle.beginFill();
        assertTrue(yertle.isFilling());
        yertle.forward(100);
        yertle.left(90);
        yertle.forward(100);
        yertle.left(90);
        yertle.forward(100);
        yertle.left(90);
        yertle.forward(100);
        yertle.left(90);
        List<Vector2D> path = yertle.getFillPath();
        yertle.speed(7);
        assertTrue(yertle.isFilling());
        assertEquals(path, yertle.getFillPath());
        yertle.endFill();

        assertFalse(yertle.isFilling());

    }

    @Test
    void getSpeedReturnsCurrentSpeed() {
        Turtle yertle = new Turtle();
        yertle.speed(6);
        assertEquals(6, yertle.getSpeed());
    }

    // invalid and out-of-range speed values.
    @Test
    void speedHandlesZeroRoundingAndOutOfRangeValues() {
        Turtle yertle = new Turtle();
        yertle.speed(0);
        assertEquals(0, yertle.getSpeed());
        // Numeric values below 0.5 or at or above 10.5 are converted to 0.
        yertle.speed(-1);
        assertEquals(0, yertle.getSpeed());

        yertle.speed(10.5);
        assertEquals(0, yertle.getSpeed());

        yertle.speed(0.49);
        assertEquals(0, yertle.getSpeed());

        // Values from 0.5 up to, but not including, 10.5 are rounded and stored as an integer.        
        yertle.speed(10.0);
        assertEquals(10, yertle.getSpeed());

        yertle.speed(0.5);
        assertEquals(1, yertle.getSpeed());

        yertle.speed(10.2);
        assertEquals(10, yertle.getSpeed());

        yertle.speed(7.5);
        assertEquals(8, yertle.getSpeed());
    }

    @Test
    void speedStoresSupportedLevels() {
        Turtle yertle = new Turtle();
        yertle.speed(1);
        assertEquals(1, yertle.getSpeed());

        yertle.speed(5);
        assertEquals(5, yertle.getSpeed());

        yertle.speed(10);
        assertEquals(10, yertle.getSpeed());        
    }

    // A normal pen-down movement records a Movement.
    @Test
    void penDownMovementsAreRecorded() {
        Turtle yertle = new Turtle();
        yertle.penDown();
        yertle.forward(100);
        List<Movement> movements = yertle.getMovements();
        Vector2D from = movements.get(0).getFrom();
        Vector2D to = movements.get(0).getTo();
        boolean penState = movements.get(0).isPenDown();

        assertEquals(1, movements.size());
        assertEquals(new Vector2D(0, 0), from);
        assertEquals(new Vector2D(100, 0), to);
        assertTrue(penState);
    }

    // verify pen-up movement is also recorded as a Movement, but does not create a LineSegment.
    @Test
    void penUpMovementIsRecordedWithoutLineSegment(){
        Turtle yertle = new Turtle();
        yertle.penUp();
        yertle.forward(100);

        List<Movement> movements = yertle.getMovements();

        assertEquals(1, movements.size());
        Vector2D from = movements.get(0).getFrom();
        Vector2D to = movements.get(0).getTo();
        boolean penState = movements.get(0).isPenDown();

        assertEquals(new Vector2D(0, 0), from);
        assertEquals(new Vector2D(100, 0), to);
        assertFalse(penState);
        assertEquals(0, yertle.getSegments().size());

    }

    // multiple movements are recorded in the correct order, including a mix of pen-down and pen-up movement.
    @Test
    void movementsAreRecordedInCommandOrder() {
        Turtle yertle = new Turtle();
        yertle.forward(100);
        yertle.penUp();
        yertle.forward(50); 

        List<Movement> movements = yertle.getMovements();

        assertEquals(2, movements.size());

        Vector2D from = movements.get(0).getFrom();
        Vector2D to = movements.get(0).getTo();
        boolean penState = movements.get(0).isPenDown();

        assertEquals(new Vector2D(0, 0), from);
        assertEquals(new Vector2D(100, 0), to);
        assertTrue(penState);

        from = movements.get(1).getFrom();
        to = movements.get(1).getTo();
        penState = movements.get(1).isPenDown();

        assertEquals(new Vector2D(100, 0), from);
        assertEquals(new Vector2D(150, 0), to);
        assertFalse(penState);

        assertEquals(1, yertle.getSegments().size());        
    }

    @Test
    void filledPolygonRecordsCompletionMovementIndex() {
        Turtle turtle = new Turtle();

        turtle.beginFill();
        turtle.forward(100);      // movement 0
        turtle.left(120);
        turtle.forward(100);      // movement 1
        turtle.left(120);
        turtle.forward(100);      // movement 2
        turtle.endFill();

        FilledPolygon polygon = turtle.getFilledPolygons().get(0);

        assertEquals(2, polygon.getCompletionMovementIndex());
    }    
}