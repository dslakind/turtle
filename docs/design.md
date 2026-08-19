# Design — v1 Class Diagram

Essential classes for Epic 1 (headless core model). `Screen` rendering comes in Epic 2.

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
        +home()
    }
    class Pen {
        -boolean isDown
        -Color color
        -double width
        +penUp()
        +penDown()
        +setColor(color)
        +setWidth(width)
    }
    class Vector2D {
        -double x
        -double y
        +add(Vector2D) Vector2D
        +distanceTo(Vector2D) double
    }
    class LineSegment {
        -Vector2D from
        -Vector2D to
        -Color color
        -double width
    }
    class Screen {
        +render(Turtle)
    }

    Turtle "1" *-- "1" Pen : has
    Turtle "1" *-- "*" LineSegment : records
    Turtle ..> Vector2D : uses
    LineSegment "1" *-- "2" Vector2D : from/to
    Screen ..> Turtle : reads segments from
```

## Notes / Decisions

- `Turtle` is self-contained in v1 — no dependency on `Screen`. It tracks its own position, heading, `Pen`, and drawn `LineSegment` history. This keeps Epic 1 fully headless and unit-testable.
- `Pen` is a separate object (not fields on `Turtle`) to mirror Python's `TPen` mixin and keep motion logic decoupled from drawing-style logic.
- `Vector2D` is immutable — operations return new instances rather than mutating in place.
- `Screen` (Epic 2) is a pure renderer: it reads a `Turtle`'s recorded `LineSegment`s and paints them; it does not own turtle state.
