package turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class TestPen {
    @Test
    void testDefaultPenState() {
        Pen pen = new Pen();
        assertEquals(new Pen(true, Color.BLACK, 1), pen);
    }

    @Test
    void testPenUp() {
        Pen pen = new Pen();
        pen.penUp();
        assertEquals(new Pen(false, Color.BLACK, 1), pen);
    }

    @Test
    void testPenDown() {
        Pen pen = new Pen();
        pen.penUp();
        pen.penDown();
        assertEquals(new Pen(true, Color.BLACK, 1), pen);
    }

    @Test
    void testSetWidth() {
        Pen pen = new Pen();
        pen.setWidth(5);
        assertEquals(new Pen(true, Color.BLACK, 5), pen);
    }

    @Test
    void testSetColor() {
        Pen pen = new Pen();
        pen.setColor(Color.BLUE);
        assertEquals(new Pen(true, Color.BLUE, 1), pen);
    }

    @Test
    void testPenValueContract() {
        Pen pen = new Pen(true, Color.RED, 2);
        Pen equalPen = new Pen(true, Color.RED, 2);

        assertEquals(pen, pen);
        assertEquals(pen, equalPen);
        assertEquals(pen.hashCode(), equalPen.hashCode());
        assertEquals(
            "Pen [isDown=true, color=" + Color.RED + ", width=2.0]",
            pen.toString()
        );

        assertNotEquals(pen, null);
        assertNotEquals(pen, "not a pen");
        assertNotEquals(pen, new Pen(false, Color.RED, 2));
        assertNotEquals(pen, new Pen(true, Color.BLUE, 2));
        assertNotEquals(pen, new Pen(true, Color.RED, 3));
    }

    @Test
    void setWidth_zeroOrNegative_throws() {
        Pen pen = new Pen();
        assertThrows(IllegalArgumentException.class, () -> pen.setWidth(0));
        assertThrows(IllegalArgumentException.class, () -> pen.setWidth(-1));
    }
}
