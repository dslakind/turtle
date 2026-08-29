# Turtle

A small Java 21 graphics library inspired by Python's
[`turtle`](https://docs.python.org/3/library/turtle.html) module. The
headless `Turtle` model records movement, pen styling, fills, and animation
history; Swing/Java2D renders that history in a window or a headless
`BufferedImage`.

## Status

**Epics 1–6 are complete.** The current implementation includes:

- Cartesian turtle movement and heading;
- pen-up and pen-down movement;
- per-segment color and width;
- completed polygon fills with independent fill color;
- centered turtle-to-Swing coordinate mapping;
- timer-driven movement animation and a visible turtle cursor;
- speed levels `0` through `10`;
- headless model, rendering, fill, cursor, and animation tests; and
- graphical example programs under `turtle.demo`.

The full Maven test suite currently contains 123 passing tests, and JaCoCo enforces minimums of 95% instruction coverage and 85% branch coverage for the core library. The implementation roadmap is complete; release preparation is in progress. See [PLAN.md](PLAN.md) for the roadmap, [CHANGELOG.md](CHANGELOG.md) for release history, and [docs/design.md](docs/design.md) for the current architecture.

## Quick start

Create the screen before issuing movement commands when you want to see those
commands animate:

```java
import java.awt.Color;

import javax.swing.SwingUtilities;

import turtle.Screen;
import turtle.Turtle;

public class TurtleExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Turtle turtle = new Turtle();
            Screen screen = new Screen(turtle);
            screen.show();

            turtle.speed(5);          // 0 = instant; 1–10 = slow-to-fast
            turtle.penColor(Color.BLUE);
            turtle.penWidth(3);
            turtle.fillColor(Color.CYAN);

            turtle.beginFill();
            for (int i = 0; i < 4; i++) {
                turtle.forward(100);
                turtle.left(90);
            }
            turtle.endFill();
        });
    }
}
```

All turtle commands update the model immediately. The Swing canvas then reveals
recorded movements in command order. Commands issued before the canvas is
constructed are treated as already visible, which is useful when animation is
not wanted.

## Core API

### Motion and heading

- `forward(distance)` and `backward(distance)`
- `left(angle)` and `right(angle)`
- `goTo(x, y)`
- `home()`
- `setHeading(angle)`
- `getPosition()` and `getHeading()`

The turtle starts at `(0, 0)`, facing east at heading `0`. Positive headings
turn counter-clockwise. `left` and `right` normalize headings to
`[0, 360)`; `setHeading` preserves the supplied value. `home()` returns to
the origin without resetting the heading or clearing drawing history.

### Pen and fill

- `penUp()` and `penDown()`
- `penColor(Color)`
- `penWidth(width)`
- `fillColor(Color)`
- `beginFill()` and `endFill()`
- `getSegments()` and `getFilledPolygons()`

`LineSegment` captures pen color and width when a stroke is recorded.
`FilledPolygon` captures ordered turtle-space points and fill color when
`endFill()` completes a valid path. Both histories are exposed as
unmodifiable views.

Pen-up movement does not create a line segment, but it still contributes a
vertex while a fill is active. Completed fills are painted before their
outlines and remain hidden during animation until the movement that completed
the polygon is visible. Self-intersecting polygons use Java2D's fill rule, so
overlap regions may vary in the same way Python documents as
graphics-system-dependent.

### Speed and animation

- `speed(level)`
- `getSpeed()`

Speed defaults to `3`. Level `0` reveals all pending movement immediately;
levels `1` through `10` animate from slower to faster. Numeric values from
`0.5` through below `10.5` are rounded to the nearest supported level;
values outside that range become `0`.

The renderer uses the turtle's current speed while replaying pending movement,
so changing speed affects the remaining queue rather than being captured per
movement. Animation changes only visual progress: the model's final position,
heading, segments, and fills are available immediately after commands return.

## Rendering behavior

`Screen` owns a `JFrame` containing a live `TurtleCanvas`. The canvas reads
the turtle's current histories on each repaint and draws:

1. visible completed fills;
2. visible full or partial pen-down strokes; and
3. the turtle cursor.

The turtle cursor is painted last so it remains visible above fills and
strokes. Pen-up movements animate the cursor without drawing a line.

Turtle space is Cartesian and centered in the component:

```text
screenX = canvasWidth / 2 + turtleX
screenY = canvasHeight / 2 - turtleY
```

The mapping uses the component's actual width and height, including non-square
canvases.

## Build and test

Requirements:

- Java 21
- Maven 3
- A graphical desktop for the Swing demos

Compile, test, and package with:

```bash
mvn compile
mvn test
mvn package
```

Most behavior is tested without opening a window. Rendering tests paint a
`TurtleCanvas` into a `BufferedImage` and inspect pixels; tests that require
a real `JFrame` are guarded for headless environments.
### Coverage

Run the full test suite and generate the JaCoCo report with:

```bash
mvn verify
```

Open `target/site/jacoco/index.html` in a browser to inspect line and branch coverage. The report measures the core `turtle` library and excludes graphical demo entry points under `turtle.demo`.

### Release artifacts

Run a clean verification build before inspecting release artifacts:

```bash
mvn clean verify
```

The build generates:

- `target/turtle-0.1.0-SNAPSHOT.jar` — distributable library JAR
- `target/turtle-0.1.0-SNAPSHOT-javadoc.jar` — packaged API documentation
- `target/reports/apidocs/index.html` — browsable API documentation


## Run the demos

Compile first, then run any demo by its fully qualified class name:

```bash
mvn compile
java -cp target/classes turtle.demo.TurtleFeatureDemo
```

`TurtleFeatureDemo` is the best overview of animation, cursor movement,
pen-up travel, styling, fills, and the main motion API. Other included demos
are:

- `TurtleDemo`
- `TurtleFaceDemo`
- `GoldenSpiralDemo`
- `GoldenRatioSunflowerDemo`
- `KochSnowflakeConstructionDemo`
- `SierpinskiConstructionDemo`
- `SierpinskiTriangleDemo`
- `MandelbrotDemo`
- `ColoredMandelbrotDemo`

Replace `TurtleFeatureDemo` in the command with any of these class names.

## Project structure

```text
src/main/java/turtle/        core library source
src/main/java/turtle/demo/   graphical example programs
src/test/java/turtle/        JUnit tests
docs/design.md               architecture and rendering contracts
CHANGELOG.md                 release history
LICENSE                      MIT license
PLAN.md                      roadmap and retrospectives
```

## License

This project is licensed under the [MIT License](LICENSE).