package turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class TestLineSegment {

    @Test
    void testLineSegmentConstructorStoresFields() {
        Vector2D v1 = new Vector2D(0, 0);
        Vector2D v2 = new Vector2D(1, 1);
        LineSegment segment = new LineSegment(v1, v2, Color.BLACK, 1);
        assertEquals(Color.BLACK, segment.getColor());
        assertEquals(v1, segment.getFrom());
        assertEquals(v2, segment.getTo());
        assertEquals(1, segment.getWidth());
    }

    @Test
    void testLineSegmentConstructorValidation() {
        Vector2D v1 = new Vector2D(0, 0);
        Vector2D v2 = new Vector2D(1, 1);
        assertThrows(NullPointerException.class, () -> new LineSegment(null, v2, Color.BLACK, 1));
        assertThrows(NullPointerException.class, () -> new LineSegment(v1, null, Color.BLACK, 1));
        assertThrows(NullPointerException.class, () -> new LineSegment(v1, v2, null, 1));
        assertThrows(IllegalArgumentException.class, () -> new LineSegment(v1, v2, Color.BLACK, 0));
        assertThrows(IllegalArgumentException.class, () -> new LineSegment(v1, v2, Color.BLACK, -1));
    }

    @Test
    void testLineSegmentEquals() {
        Vector2D v1 = new Vector2D(0, 0);
        Vector2D v2 = new Vector2D(1, 1);
        LineSegment segment1 = new LineSegment(v1, v2, Color.BLACK, 1);
        LineSegment segment2 = new LineSegment(v1, v2, Color.BLACK, 1);
        LineSegment segment3 = new LineSegment(v1, v2, Color.RED, 1);
        LineSegment segment4 = new LineSegment(v2, v1, Color.BLACK, 1);
        LineSegment segment5 = new LineSegment(v1, v2, Color.BLACK, 5);

        assertEquals(segment1, segment1);
        assertEquals(segment1, segment2);
        assertNotEquals(segment2, segment3);
        assertNotEquals(segment2, segment4);
        assertNotEquals(segment2, segment5);
        assertNotEquals(segment2, null);
        assertNotEquals(segment2, "foo");
    }

    @Test
    void testLineSegmentHashCode() {
        Vector2D v1 = new Vector2D(0, 0);
        Vector2D v2 = new Vector2D(1, 1);
        LineSegment segment1 = new LineSegment(v1, v2, Color.BLACK, 1);
        LineSegment segment2 = new LineSegment(v1, v2, Color.BLACK, 1);
        assertEquals(segment1.hashCode(), segment2.hashCode());
    }

    @Test
    void testLineSegmentToString() {
        Vector2D from = new Vector2D(0, 0);
        Vector2D to = new Vector2D(1, 1);
        LineSegment segment = new LineSegment(from, to, Color.BLACK, 2);

        assertEquals(
            "LineSegment [from=" + from
                + ", to=" + to
                + ", color=" + Color.BLACK
                + ", width=2.0]",
            segment.toString()
        );
    }
}
