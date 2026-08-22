# Turtle

This repository is intended to create a library for Java that is similar in scope and speed to the Python turtle library at https://docs.python.org/3/library/turtle.html

## Status

**Epic 2 complete** — the Swing rendering layer is implemented and tested. The project now includes a `Screen` window with a custom `TurtleCanvas`, live turtle-to-canvas rendering for recorded segments, and coordinate transformation from turtle space to Swing pixel space.

See [PLAN.md](PLAN.md) for the roadmap and [docs/design.md](docs/design.md) for the class diagram.

## Usage

```java
Turtle t = new Turtle();      // origin (0,0), heading 0° (east), pen down
t.forward(100);               // move east 100 units, records a LineSegment
t.left(90);                   // turn to face north
t.forward(100);
t.penUp();
t.goTo(0, 0);                 // teleport — no segment recorded
t.penDown();
t.home();                     // return to origin and record segment

Screen screen = new Screen(t);
screen.show();
```

This opens a `JFrame` with a custom `TurtleCanvas` that paints each recorded `LineSegment` using its stored color and width.

## Requirements

- Java 21
- Maven

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

