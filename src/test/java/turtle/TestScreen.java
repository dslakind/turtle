package turtle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.Test;

class TestScreen{
    // Prefer a package-private accessor or constructor-visible test hook over reflection.

    // Story 2.1 — A TurtleScreen/Window class that opens a JFrame with a custom JPanel canvas.

    // Construct a Screen, retrieve its canvas, and verify that the canvas is a JPanel.
    @Test
    void screenHasAFrame() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());

        SwingUtilities.invokeAndWait(() -> {
            Turtle turtle = new Turtle();
            Screen screen = new Screen(turtle);

            try {
                assertNotNull(screen.getFrame());
            } finally {
                screen.getFrame().dispose();
            }
        });
    }    

    @Test
    void frameContainsTurtleCanvas() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());

        SwingUtilities.invokeAndWait(() -> {
            Turtle turtle = new Turtle();
            Screen screen = new Screen(turtle);

            JFrame frame = screen.getFrame();
            TurtleCanvas canvas = screen.getCanvas();

            try {
                assertEquals(1, frame.getContentPane().getComponentCount());
                assertSame(canvas, frame.getContentPane().getComponent(0));
            } finally {
                frame.dispose();
            }
        });
    }    

@Test
void frameIsConfiguredCorrectly() throws Exception {
    assumeFalse(GraphicsEnvironment.isHeadless());

    SwingUtilities.invokeAndWait(() -> {
        Turtle turtle = new Turtle();
        Screen screen = new Screen(turtle);
        JFrame frame = screen.getFrame();

        try {
            assertEquals("Turtle Graphics", frame.getTitle());
            assertEquals(800, frame.getWidth());
            assertEquals(600, frame.getHeight());
            assertEquals(
                JFrame.DISPOSE_ON_CLOSE,
                frame.getDefaultCloseOperation()
            );
        } finally {
            frame.dispose();
        }
    });
}
    @Test
    void showMakesFrameVisible() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless());

        SwingUtilities.invokeAndWait(() -> {
            Turtle turtle = new Turtle();
            Screen screen = new Screen(turtle);

            try {
                assertFalse(screen.getFrame().isVisible());    
                screen.show();
                assertTrue(screen.getFrame().isVisible());
            } finally {
                screen.getFrame().dispose();
            }
        });
    }

    // Story 2.2 — Canvas paints all recorded line segments from a Turtle's history via paintComponent/Graphics2D.
    // Story 2.3 — Coordinate system translation: turtle's Cartesian (0,0 = center, y-up) → Swing's pixel coords (0,0 = top-left, y-down).
    @Test
    void paintsRecordedSegmentsUsingTheirColors() {
        Turtle turtle = new Turtle();

        turtle.getPen().setColor(Color.RED);
        turtle.forward(100);

        turtle.left(90);
        turtle.getPen().setColor(Color.BLUE);
        turtle.forward(100);

        BufferedImage image = render(turtle);

        assertEquals(Color.RED.getRGB(), image.getRGB(350, 300));
        assertEquals(Color.BLUE.getRGB(), image.getRGB(400, 250));
        assertEquals(Color.WHITE.getRGB(), image.getRGB(350, 250));
    }

    @Test
    void paintsRecordedSegmentsUsingTheirWidths() {
        Turtle turtle = new Turtle();
        turtle.getPen().setWidth(1);

        turtle.forward(100);
        
        turtle.left(90);
        turtle.getPen().setWidth(5);
        turtle.forward(100);

        BufferedImage image = render(turtle);

        // Width-1 horizontal segment
        assertEquals(Color.BLACK.getRGB(), image.getRGB(350, 300));
        assertEquals(Color.WHITE.getRGB(), image.getRGB(350, 302));

        // Width-5 vertical segment
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 250));
        assertEquals(Color.BLACK.getRGB(), image.getRGB(402, 250));
        assertEquals(Color.WHITE.getRGB(), image.getRGB(407, 250));

    }

    @Test
    void doesNotPaintPenUpMovement() {
        Turtle turtle = new Turtle();
        turtle.getPen().penUp();
        turtle.forward(100);

        BufferedImage image = render(turtle);

        assertEquals(Color.WHITE.getRGB(), image.getRGB(350, 300));
    }

    @Test
    void mapsTurtleOriginToCanvasCenter() {
        Turtle turtle = new Turtle();
        turtle.forward(100);

        BufferedImage image = render(turtle);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(300, 300));
    }

    @Test
    void mapsPositiveAndNegativeXCoordinates() {
        Turtle positiveX = new Turtle();
        positiveX.forward(100);

        BufferedImage positiveImage = render(positiveX);

        assertEquals(
                Color.BLACK.getRGB(),
                positiveImage.getRGB(400, 300)
        );

        Turtle negativeX = new Turtle();
        negativeX.left(180);
        negativeX.forward(100);

        BufferedImage negativeImage = render(negativeX);

        assertEquals(
                Color.BLACK.getRGB(),
                negativeImage.getRGB(200, 300)
        );
    }

    @Test
    void mapsPositiveAndNegativeYCoordinates() {
        Turtle positiveY = new Turtle();
        positiveY.left(90);
        positiveY.forward(100);

        BufferedImage positiveImage = render(positiveY);

        assertEquals(
                Color.BLACK.getRGB(),
                positiveImage.getRGB(300, 200)
        );

        Turtle negativeY = new Turtle();
        negativeY.right(90);
        negativeY.forward(100);

        BufferedImage negativeImage = render(negativeY);

        assertEquals(
                Color.BLACK.getRGB(),
                negativeImage.getRGB(300, 400)
        );
    }

    @Test
    void usesActualCanvasWidthAndHeightForOrigin() {
        Turtle turtle = new Turtle();
        turtle.left(90);
        turtle.forward(100);

        BufferedImage image = render(turtle, 800, 600);

        // Origin
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 300));

        // Turtle (0, 100) -> Swing (400, 200)
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 200));
    }    

    private BufferedImage render(Turtle turtle) {
        return render(turtle, 600, 600);
    }

    private BufferedImage render(Turtle turtle, int width, int height) {
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        canvas.setSize(width, height);

        BufferedImage image =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        try {
            canvas.paint(graphics);
        } finally {
            graphics.dispose();
        }

        return image;
    }
}