package turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestMovement {
    // A Movement stores its from, to, and penDown values exactly as provided.
    @Test
    void movementStoresFromToPenDown() {
        Vector2D from = new Vector2D(0, 0);
        Vector2D to = new Vector2D(100, 0);
        boolean penDown = true;
        Movement movement = new Movement(from, to, penDown, 0);

        assertEquals(from, movement.getFrom());
        assertEquals(to, movement.getTo());
        assertTrue(movement.isPenDown());
        assertEquals(0, movement.getHeading());
    }
}
