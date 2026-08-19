package turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class TestVector2D {

    @Test
    void originHasZeroCoordinates() {
        Vector2D vector = new Vector2D(0, 0);
        assertEquals(0.0, vector.getX());
        assertEquals(0.0, vector.getY());
    }

    @Test
    void testDistanceTo() {
        Vector2D v1 = new Vector2D(2, 3);
        Vector2D v2 = new Vector2D(-1, -1);
        assertEquals(5.0, v1.distanceTo(v2));
        assertEquals(0.0, v1.distanceTo(v1));
        assertEquals(0.0, v2.distanceTo(v2));
    }

    @Test
    void testAddVectors() {
        Vector2D v1 = new Vector2D(2, 3);
        Vector2D v2 = new Vector2D(-1, -1);
        Vector2D v3 = new Vector2D(1, 2);
        assertEquals(v3, v1.add(v2));
        assertEquals(v2.add(v1), v1.add(v2));
        assertEquals(new Vector2D(0, 0), v3.add(new Vector2D(-1, -2)));
    }

}

