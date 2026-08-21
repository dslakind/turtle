# Design — v1 Class Diagram

Epic 1 classes are implemented. `Screen` (Epic 2) is designed but not yet implemented.

```mermaid
classDiagram
    class Turtle {
        -Vector2D position
        -double heading
        -Pen pen
        -List~LineSegment~ segments
        +forward(distance)
        +backward(distance)
        +left(angle)
        +right(angle)
        +goTo(x, y)
        +setHeading(angle)
        +penUp()
        +penDown()
        +home()
        +getPosition() Vector2D
        +getHeading() double
        +getPen() Pen
        +getSegments() List~LineSegment~
    }
    class Pen {
        -boolean isDown
        -Color color
        -double width
        +Pen()
        +penUp()
        +penDown()
        +setColor(color)
        +setWidth(width)
        +isDown() boolean
        +getColor() Color
        +getWidth() double
    }
    class Vector2D {
        -double x
        -double y
        +add(Vector2D) Vector2D
        +distanceTo(Vector2D) double
        +getX() double
        +getY() double
    }
    class LineSegment {
        -Vector2D from
        -Vector2D to
        -Color color
        -double width
        +getFrom() Vector2D
        +getTo() Vector2D
        +getColor() Color
        +getWidth() double
    }
    class Screen {
        -Turtle turtle
        -TurtleCanvas canvas
        +Screen(turtle)
        +show()
    }
    class TurtleCanvas {
        -Turtle turtle
        +TurtleCanvas(turtle)
        +paintComponent(Graphics g)
    }

    Turtle "1" *-- "1" Pen : has
    Turtle "1" *-- "*" LineSegment : records
    Turtle ..> Vector2D : uses
    LineSegment "1" *-- "2" Vector2D : from/to
    Screen "1" o-- "1" Turtle : renders live
    Screen "1" *-- "1" TurtleCanvas : owns
    TurtleCanvas "1" o-- "1" Turtle : reads segments
```

## Notes / Decisions

- `Turtle` is self-contained in v1 — no dependency on `Screen`. It tracks its own position, heading, `Pen`, and drawn `LineSegment` history. This keeps Epic 1 fully headless and unit-testable.
- `Pen` is a separate object (not fields on `Turtle`) to mirror Python's `TPen` mixin and keep motion logic decoupled from drawing-style logic.
- `Vector2D` is immutable — operations return new instances rather than mutating in place.
- `LineSegment` is immutable — all fields are `final`; captures pen color and width at draw time.
- `Turtle` is mutable with getters. `getSegments()` returns an unmodifiable view.
- Heading is stored in degrees, normalised to `[0, 360)` after every `left`/`right` call. Conversion to radians happens only inside `forward` when computing trig.
- Heading convention: 0° = east, increases counter-clockwise (standard math / Python turtle).
- `forward(0)` is a no-op — returns early before computing position or recording a segment.
- `forward` delegates to a private `goTo(Vector2D)` overload; the public `goTo(double, double)` also delegates to it. All movement and segment-recording logic lives in one place.
- `Screen` (Epic 2) is a pure renderer: it reads a `Turtle`'s recorded `LineSegment`s and paints them; it does not own turtle state.
- `Screen` holds a **live `Turtle` reference** (constructor: `Screen(Turtle)`). `paintComponent` calls `turtle.getSegments()` on each repaint — no snapshot is passed by the caller. Rationale: keeps the API minimal, matches Python turtle's live screen model, and allows Epic 4 animation via a timer + `repaint()` with no API change. A snapshot approach would require callers to re-pass segments on every update with no benefit at this scale.
- `TurtleCanvas extends JPanel` is package-private and owned by `Screen`. It overrides `paintComponent` to iterate `turtle.getSegments()` and draw each via `Graphics2D`. It also owns the coordinate transform (Story 2.3): turtle Cartesian (origin = centre, y-up) → Swing pixel (origin = top-left, y-down).
