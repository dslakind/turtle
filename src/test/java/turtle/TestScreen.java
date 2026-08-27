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

    /* Test Story 4.4 — Canvas renders completed polygons before their outlines, 
        using the stored fill color. */
    
    // A triangle’s interior pixel has the expected fill color.
    @Test
    void filledPolygonInteriorPixelHasFillColor() {
        Turtle turtle = new Turtle();
        turtle.fillColor(Color.RED);
        turtle.beginFill();
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);        
        turtle.endFill();

        BufferedImage image = render(turtle, 600, 600);
        assertEquals(Color.RED.getRGB(), image.getRGB(350, 270));
        // A pixel outside the triangle remains the background color.
        assertEquals(Color.WHITE.getRGB(), image.getRGB(450, 350));
    }

    // The outline remains visible after filling.
    @Test
    void shapeOutlineRemainsVisibleAfterFilling() {
        Turtle turtle = new Turtle();
        turtle.fillColor(Color.RED);
        turtle.beginFill();
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);        
        turtle.endFill();

        BufferedImage image = render(turtle, 600, 600);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(300, 300));
        assertEquals(Color.RED.getRGB(), image.getRGB(350, 270));
        assertEquals(Color.BLACK.getRGB(), image.getRGB(350, 300));
    }

    // The fill color used is the one active at end_fill() time.
    @Test
    void filledPolygonUsesColorActiveAtEndFill() {
        Turtle turtle = new Turtle();
        turtle.fillColor(Color.RED);
        turtle.beginFill();
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);        
        turtle.endFill();

        BufferedImage image = render(turtle, 600, 600);

        assertEquals(Color.RED.getRGB(), image.getRGB(350, 270));
    }

    // Multiple polygons render independently with their respective fill colors.
    @Test
    void multiplePolygonsRenderIndependently() {
        // draw triangle red
        Turtle turtle = new Turtle();
        turtle.fillColor(Color.RED);
        turtle.beginFill();
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);        
        turtle.endFill();

        // draw square blue
        turtle.penUp();
        turtle.goTo(200, 50);
        turtle.fillColor(Color.BLUE);
        turtle.penDown();

        turtle.beginFill();
        turtle.forward(100);
        turtle.left(90);
        turtle.forward(100);
        turtle.left(90);
        turtle.forward(100);
        turtle.left(90);
        turtle.forward(100);
        turtle.left(90);
        turtle.endFill();

        // test fill colors
        BufferedImage image = render(turtle, 600, 600);

        // Red triangle centered at origin: check pixel inside it
        assertEquals(Color.RED.getRGB(), image.getRGB(350, 270));
        // Blue square at (200, 50): should be to the right, check pixel inside it
        assertEquals(Color.BLUE.getRGB(), image.getRGB(510, 190));
    }

    // A polygon with a pen-up section fills continuously while no line is drawn across that section.
    @Test
    void polygonWithPenUpSectionFillsWithNoLineDrawn() {
        Turtle turtle = new Turtle();
        turtle.fillColor(Color.BLUE);
        turtle.penUp();
        turtle.beginFill();
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);        
        turtle.endFill();

        BufferedImage image = render(turtle, 600, 600);

        // Triangle centered at origin: vertices at (300, 300), (400, 300), (350, 213)
        // Check pixel inside triangle
        assertEquals(Color.BLUE.getRGB(), image.getRGB(307, 295));
    }

    // Positive and negative y coordinates map correctly using the existing transform.
    @Test
    void polygonPositiveAndNegativeYCoordinatesMapCorrectly() {
        Turtle turtle = new Turtle();
        turtle.penUp();

        // Positive y triangle — should appear above center.
        turtle.goTo(-50, 50);
        turtle.fillColor(Color.RED);
        turtle.beginFill();
        turtle.goTo(50, 50);
        turtle.goTo(0, 150);
        turtle.endFill();

        // Negative y triangle — should appear below center.
        turtle.goTo(-50, -50);
        turtle.fillColor(Color.BLUE);
        turtle.beginFill();
        turtle.goTo(50, -50);
        turtle.goTo(0, -150);
        turtle.endFill();

        BufferedImage image = render(turtle, 600, 600);

        // Positive turtle y maps upward.
        assertEquals(Color.RED.getRGB(), image.getRGB(300, 220));

        // Negative turtle y maps downward.
        assertEquals(Color.BLUE.getRGB(), image.getRGB(300, 380));
    }

    // A non-square canvas still maps polygon vertices correctly.   
    @Test
    void nonSquareCanvasMapsPolygonCorrectly() {
        Turtle turtle = new Turtle();
        turtle.fillColor(Color.RED);
        turtle.beginFill();

        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);      

        turtle.endFill();

        BufferedImage image = render(turtle, 600, 800);

        assertEquals(Color.RED.getRGB(), image.getRGB(350, 370));
        assertEquals(Color.WHITE.getRGB(), image.getRGB(350, 270));
    } 

    /* Story 5.2 — Incremental redraw/timer-based animation of movement */
    // test partial rendering before adding the timer
    @Test
    void partiallyRevealsCurrentSegment() {
        Turtle turtle = new Turtle();
        turtle.speed(10);
        turtle.forward(100);

        BufferedImage image = render(turtle, 600, 600, 0, 0.5);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(325, 300));
        assertEquals(Color.WHITE.getRGB(), image.getRGB(375, 300));
    }    

    @Test
    void completedAnimationStateRevealsAllSegments() {
        Turtle turtle = new Turtle();
        turtle.speed(10);
        turtle.forward(100);
        turtle.right(90);
        turtle.forward(100);

        BufferedImage image = render(turtle, 600, 600);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(325, 300));
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 320));
        assertEquals(Color.WHITE.getRGB(), image.getRGB(475, 300));
    }

    @Test
    void animationCursorRevealsSegmentsInOrder() {
        Turtle turtle = new Turtle();
        turtle.speed(10);
        turtle.forward(100);
        turtle.right(90);
        turtle.forward(100);
        turtle.right(90);
        turtle.forward(100);

        BufferedImage image = render(turtle, 600, 600, 1, 0.5);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(375, 300)); // segment 0 fully visible
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 325)); // first half of segment 1
        assertEquals(Color.WHITE.getRGB(), image.getRGB(400, 375)); // second half of segment 1
        assertEquals(Color.WHITE.getRGB(), image.getRGB(350, 400)); // segment 2 hidden
    }

    // A segment at 0.0 progress advances to approximately 0.1 after one call.
    @Test
    void testAdvanceAnimationStep() {
        Turtle turtle = new Turtle();
        turtle.speed(10);
        turtle.forward(100);
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        canvas.setAnimationProgress(0, 0.0);
        canvas.advanceAnimationStep();

        BufferedImage image = render(canvas, 600, 600);
        assertEquals(Color.BLACK.getRGB(), image.getRGB(305, 300)); 
        assertEquals(Color.WHITE.getRGB(), image.getRGB(325, 300)); 
    }

    @Test
    void advanceAnimationStepMovesToNextSegment() {
        Turtle turtle = new Turtle();
        turtle.speed(10);
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.forward(100);
        turtle.right(90);
        turtle.forward(100);

        canvas.setAnimationProgress(0, 0.95);
        canvas.advanceAnimationStep();
        canvas.advanceAnimationStep();

        BufferedImage image = render(canvas, 600, 600);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(375, 300)); 
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 305)); 
        assertEquals(Color.WHITE.getRGB(), image.getRGB(400, 325)); 
    }

    @Test
    void advanceAnimationStepDoesNothingWhenComplete() {
        Turtle turtle = new Turtle();
        turtle.speed(10);
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.forward(100);

        canvas.setAnimationProgress(turtle.getMovements().size(), 0.0);
        canvas.advanceAnimationStep();

        BufferedImage image = render(canvas, 600, 600);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(300, 300)); 
        assertEquals(Color.BLACK.getRGB(), image.getRGB(350, 300)); 
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 300)); 
        assertEquals(Color.WHITE.getRGB(), image.getRGB(400, 310)); 
    }

    @Test
    void speedZeroRevealsPendingSegmentImmediately() {
        Turtle turtle = new Turtle();
        turtle.speed(0);
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.forward(100);

        canvas.advanceAnimationStep();
        
        BufferedImage image = render(canvas, 600, 600);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(300, 300)); 
        assertEquals(Color.BLACK.getRGB(), image.getRGB(350, 300));
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 300)); 
    }

    // At the same number of animation steps, a higher speed reveals more of the segment than a lower speed.
    @Test
    void higherSpeedAdvancesFartherPerAnimationStep() {
        Turtle turtle = new Turtle();
        turtle.speed(1);
        turtle.forward(100);
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        canvas.setAnimationProgress(0, 0.0);
        canvas.advanceAnimationStep();

        BufferedImage image = render(canvas, 600, 600);
        assertEquals(Color.BLACK.getRGB(), image.getRGB(300, 300)); 
        assertEquals(Color.WHITE.getRGB(), image.getRGB(325, 300));

        Turtle yertle = new Turtle();
        yertle.speed(10);
        yertle.forward(100);
        TurtleCanvas yertleCanvas = new TurtleCanvas(yertle);
        yertleCanvas.setAnimationProgress(0, 0.0);
        yertleCanvas.advanceAnimationStep();

        BufferedImage image2 = render(yertleCanvas, 600, 600);
        assertEquals(Color.BLACK.getRGB(), image2.getRGB(305, 300)); 
        assertEquals(Color.WHITE.getRGB(), image2.getRGB(325, 300));
    }

    @Test
    void testTimerStartAndStop() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        canvas.addNotify();
        assertTrue(canvas.isAnimationTimerRunning());
        canvas.removeNotify();
        assertFalse(canvas.isAnimationTimerRunning());
    }

    @Test
    void animationTickAdvancesVisibleProgress() {
        Turtle turtle = new Turtle();
        turtle.speed(10);
        turtle.forward(100);
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        canvas.setAnimationProgress(0, 0.0);
        canvas.animationTick();

        BufferedImage image = render(canvas, 600, 600);
        assertEquals(Color.BLACK.getRGB(), image.getRGB(301, 300)); 
        assertEquals(Color.BLACK.getRGB(), image.getRGB(305, 300));
        assertEquals(Color.WHITE.getRGB(), image.getRGB(325, 300));        
    }

    @Test
    void newMovementBecomesPendingAnimation() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.forward(100);

        BufferedImage image = render(canvas, 600, 600);
        assertEquals(Color.WHITE.getRGB(), image.getRGB(350, 300));        

        canvas.animationTick();
        BufferedImage image2 = render(canvas, 600, 600);
        assertEquals(Color.BLACK.getRGB(), image2.getRGB(301, 300));        
    }

    @Test
    void pendingMovementsAnimateInRecordedOrder() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.forward(100);
        turtle.right(90);
        turtle.forward(100);
        turtle.speed(10);
        for (int i = 0; i < 11; i++) {
            canvas.animationTick();
        }

        BufferedImage image = render(canvas, 600, 600);
        assertEquals(Color.BLACK.getRGB(), image.getRGB(350, 300));        
        assertEquals(Color.BLACK.getRGB(), image.getRGB(400, 305));        
        assertEquals(Color.WHITE.getRGB(), image.getRGB(400, 325));        
    }

    @Test
    void penUpMovementDoesNotShiftLineAnimation() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        turtle.penDown();
        turtle.forward(100);

        turtle.penUp();
        turtle.forward(50);

        turtle.penDown();
        turtle.forward(100);

        canvas.setAnimationProgress(2, 0.5);

        BufferedImage image = render(canvas, 600, 600);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(350, 300)); 
        assertEquals(Color.WHITE.getRGB(), image.getRGB(425, 300)); 
        assertEquals(Color.BLACK.getRGB(), image.getRGB(475, 300)); 
        assertEquals(Color.WHITE.getRGB(), image.getRGB(525, 300)); 
    }

    @Test
    void visibleTurtlePositionInterpolatesCurrentMovement() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.penUp();
        turtle.forward(100);
        canvas.setAnimationProgress(0, 0.5);
        assertEquals(new Vector2D(50, 0), canvas.getVisibleTurtlePosition());
        assertEquals(new Vector2D(100, 0), turtle.getPosition());
    }

    @Test
    void visibleTurtlePositionUsesFinalModelPositionWhenAnimationComplete() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.forward(100);
        canvas.setAnimationProgress(turtle.getMovements().size(), 0.0);
        assertEquals(turtle.getPosition(), canvas.getVisibleTurtlePosition());
    }

    @Test
    void visibleTurtlePositionInterpolatesLaterMovement() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.forward(100);
        turtle.penUp();
        turtle.forward(50);

        canvas.setAnimationProgress(1, 0.5);

        assertEquals(new Vector2D(125, 0), canvas.getVisibleTurtlePosition());
        assertEquals(new Vector2D(150, 0), turtle.getPosition());
    }

    @Test
    void penUpMovementMovesVisibleTurtleWithoutDrawingLine() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.penUp();
        turtle.forward(50);

        canvas.setAnimationProgress(0, 0.5);
        BufferedImage image = render(canvas, 600, 600);

        assertEquals(Color.WHITE.getRGB(), image.getRGB(350, 300)); 
        assertEquals(new Vector2D(25, 0), canvas.getVisibleTurtlePosition());
    }

    @Test
    void movementRecordsHeadingAtMovementTime() {
        Turtle turtle = new Turtle();
        turtle.forward(100);
        turtle.right(90);
        turtle.forward(100);
        
        Movement movement1 = turtle.getMovements().get(0);
        Movement movement2 = turtle.getMovements().get(1);

        assertEquals(0, movement1.getHeading(),  1e-9);
        assertEquals(270, movement2.getHeading(), 1e-9);
    }

    @Test
    void visibleTurtleHeadingUsesCurrentMovementHeading() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.forward(100);
        turtle.right(90);
        turtle.forward(100);
        
        canvas.setAnimationProgress(0, 0.5);
        assertEquals(0, canvas.getVisibleTurtleHeading());
        assertEquals(270, turtle.getHeading());
    }

    @Test
    void visibleTurtleTriangleIsDrawnAtInterpolatedPosition() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        turtle.penUp();
        turtle.forward(50);

        canvas.setAnimationProgress(0, 0.5);
        BufferedImage image = render(canvas, 600, 600);

        assertEquals(Color.BLACK.getRGB(), image.getRGB(325, 300)); 
    }

    @Test
    void visibleTurtleTrianglePointsInMovementHeading() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        // Point the turtle upward and move without drawing a line.
        turtle.penUp();
        turtle.setHeading(90);
        turtle.forward(50);

        // Halfway through the movement, the visible turtle is at (0, 25),
        // which maps to screen position (300, 275).
        canvas.setAnimationProgress(0, 0.5);

        BufferedImage image = render(canvas, 600, 600);

        // The nose of a 90-degree turtle should extend upward.
        assertEquals(Color.BLACK.getRGB(), image.getRGB(300, 270));

        // A point beyond the rear of the triangle should remain white.
        assertEquals(Color.WHITE.getRGB(), image.getRGB(300, 285));
    }    

    
    @Test
    void visibleTurtleHeadingUsesFinalModelHeadingWhenAnimationComplete() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        turtle.forward(100);
        turtle.right(90);

        canvas.setAnimationProgress(turtle.getMovements().size(), 0.0);
        
        assertEquals(270, canvas.getVisibleTurtleHeading(), 1e-9);
    }

    @Test
    void completedFillRemainsHiddenUntilItsMovementsFinish() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        turtle.penColor(Color.BLUE);
        turtle.fillColor(Color.RED);

        turtle.beginFill();
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.endFill();

        // The model has completed the polygon, but visually we are only
        // halfway through the first movement.
        canvas.setAnimationProgress(0, 0.5);

        BufferedImage image = render(canvas, 600, 600);

        // A point inside the completed polygon should still be white
        // because the fill should not appear until its movements finish.
        assertEquals(Color.WHITE.getRGB(), image.getRGB(350, 270));
    }

    @Test
    void completedFillAppearsAfterItsMovementsFinish() {
        Turtle turtle = new Turtle();
        TurtleCanvas canvas = new TurtleCanvas(turtle);

        turtle.penColor(Color.BLUE);
        turtle.fillColor(Color.RED);

        turtle.beginFill();
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.left(120);
        turtle.forward(100);
        turtle.endFill();

        // All three movements have finished visually.
        canvas.setAnimationProgress(turtle.getMovements().size(), 0.0);

        BufferedImage image = render(canvas, 600, 600);

        // A point inside the triangle should now show the fill.
        assertEquals(Color.RED.getRGB(), image.getRGB(350, 270));
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
    
    private BufferedImage render(TurtleCanvas canvas, int width, int height) {
        // TurtleCanvas canvas = new TurtleCanvas(turtle);
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

    private BufferedImage render(Turtle turtle, int width, int height, int segmentIndex, double fraction) {
        TurtleCanvas canvas = new TurtleCanvas(turtle);
        canvas.setSize(width, height);
        canvas.setAnimationProgress(segmentIndex, fraction);

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