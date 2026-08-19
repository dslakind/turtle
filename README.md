# Turtle

This repository is intended to create a library for Java that is similar in scope and speed to the Python turtle library at https://docs.python.org/3/library/turtle.html

## Status

**Epic 1 complete** — the headless core state model is implemented and fully unit-tested (15 tests, all passing). Epic 2 (Swing rendering) is next. See [PLAN.md](PLAN.md) for the roadmap and [docs/design.md](docs/design.md) for the class diagram.

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

List<LineSegment> segments = t.getSegments(); // read by Screen in Epic 2
```

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

