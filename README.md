# Turtle

This repository contains a small Java 21 library inspired by Python's
`turtle` module. The headless `Turtle` model records movement, pen styling,
and completed fills; Swing/Java2D renders that history in a window or a
headless `BufferedImage`.

## Status

**Epics 1–4 complete** — motion, pen styling, Swing rendering, polygon
filling, coordinate mapping, and headless pixel tests are implemented and
tested. Story 4.5 uses `BufferedImage` assertions for polygon interiors,
outlines, pen-up fill paths, positive/negative Y, and non-square canvases.

See [PLAN.md](PLAN.md) for the roadmap and [docs/design.md](docs/design.md) for the class diagram.

## Usage

```java
Turtle t = new Turtle();      // origin (0,0), heading 0 (east), pen down
t.forward(100);               // move east and record a LineSegment
t.left(90);                   // turn counter-clockwise
t.forward(100);
t.penUp();
t.goTo(0, 0);                 // move without recording a segment
t.penDown();
t.home();                     // return to origin and record a segment

t.penColor(Color.BLUE);
t.fillColor(Color.CYAN);
t.beginFill();
for (int i = 0; i < 4; i++) {
	t.forward(80);
	t.left(90);
}
t.endFill();                 // publishes and renders the completed polygon

Screen screen = new Screen(t);
screen.show();
```

`Screen` opens a `JFrame` containing a `TurtleCanvas`. The canvas keeps a live
reference to the turtle, paints completed polygons before line segments, and
maps turtle coordinates using:

```text
screenX = canvasWidth / 2 + turtleX
screenY = canvasHeight / 2 - turtleY
```

The model is also usable without Swing. `getSegments()` and
`getFilledPolygons()` expose unmodifiable histories, while `LineSegment` and
`FilledPolygon` are immutable snapshots of drawing data.

Fill color is captured when `endFill()` completes the polygon. Movement while
the pen is up still contributes vertices to an active fill path, but creates
no line segment. Self-intersecting polygons, such as a five-point star, follow
Java2D's fill rule, so overlap regions may remain unfilled.

Animation is a rendering concern. Movement updates the headless turtle model
immediately, while Swing may reveal the resulting segments progressively. New
commands are accepted immediately and displayed in recorded command order.
The animation cursor and partial-segment progress belong to Swing-side state,
not to `Turtle`.
Speed `0` skips visual animation and displays the current final state at once.

## Requirements

- Java 21
- Maven

The graphical demo can be run with:

```bash
mvn compile
java -cp target/classes turtle.TurtleDemo
```

## Build & Test

```bash
mvn compile
mvn test
```

## Project Structure

```
src/main/java/turtle   # library source
src/test/java/turtle   # unit tests
```

