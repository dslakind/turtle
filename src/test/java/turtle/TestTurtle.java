package turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
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
        yertle.backward(100);
        // penUp() then penDown() then forward → one segment
        yertle.penDown();
        yertle.forward(100);
        assertEquals(1, yertle.getSegments().size());
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
}