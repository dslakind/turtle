# Java Turtle Graphics Library — Project Plan

## Overview
A Java library that replicates the core motion & drawing behavior of Python's [`turtle`](https://docs.python.org/3/library/turtle.html) module. Rendering is done via Java Swing/Java2D. Built with Maven, targeting Java 21, tested with JUnit 5.

## Decisions Log
| Decision | Choice |
|---|---|
| Build tool | Maven 3 (maven-surefire-plugin 3.2.5) |
| Java version | 21 (LTS) |
| Rendering | Java Swing / Java2D window |
| Test framework | JUnit Jupiter 5.10.2 |
| Base package | `turtle` |
| API scope (v1) | Core motion & drawing only (movement, heading, pen up/down, color, speed) |
| Version control | Git, initialized locally |
| Turtle mutability | Mutable with getters (`getPosition`, `getHeading`, `getPen`, `getSegments`) |
| Angle units | Degrees externally; converted to radians internally only for trig |
| Heading convention | 0° = east, increases counter-clockwise, normalised to `[0, 360)` |
| Segment list access | `getSegments()` returns an unmodifiable view |
| `Vector2D` mutability | Immutable — all operations (`add`, etc.) return new instances |
| `LineSegment` mutability | Immutable — all fields `final`; captures pen color & width at draw time |
| `forward(0)` behaviour | No-op — returns before computing position or recording a segment |
| `forward` / `goTo` delegation | Public `goTo(double, double)` and `forward` both delegate to private `goTo(Vector2D)`; all segment-recording logic lives there |
| `setHeading` normalisation | Does **not** normalise to `[0, 360)` — only `left`/`right` normalise |
| `home()` heading | Does **not** reset heading — only resets position to origin |
| `Pen.setWidth` validation | Throws `IllegalArgumentException` for zero or negative values |
| `Pen` value equality | Implements `equals`/`hashCode` (by `isDown`, `color`, `width`) to simplify test assertions |
| `Screen` role | Pure renderer (Epic 2) — reads `Turtle.getSegments()` and paints; owns no turtle state |
| `Screen` coupling | Holds a **live `Turtle` reference** — `paintComponent` calls `turtle.getSegments()` on each repaint; no snapshot passing. Enables animation (Epic 4) via a timer + `repaint()` with no API change. |

Out of scope for v1 (candidates for later epics): screen/window configuration (bgcolor, title, size), shapes/stamps, event handlers (onclick/onkey), multiple simultaneous turtles, undo/clone, text/write().

## How We'll Work (Agile, solo-adapted)
Since there's no team to coordinate, we'll keep the ceremony lightweight but still get the benefits of Agile: small increments, working software early and often, and regular reflection.

- **Backlog**: The epics/stories below are the product backlog, roughly ordered by priority/dependency.
- **Sprints**: Instead of calendar time-boxing, each "sprint" = one epic (or a coherent chunk of one). We finish a sprint when its stories meet their acceptance criteria and tests pass.
- **Definition of Done** for every story:
  1. Code implemented (by you, with my guidance/review — I won't write most of it).
  2. Unit tests written first or alongside (TDD where practical) and passing.
  3. `mvn test` is green.
  4. Public API has Javadoc.
  5. Quick self-review: does the behavior match the Python `turtle` equivalent (where applicable)?
- **Sprint Review**: After each epic, we do a short walkthrough — run a demo script exercising the new feature.
- **Retro**: A few bullet points after each epic — what was tricky, what to change next time. I'll prompt you for this.
- **Kanban-style tracking**: Use the checkboxes below as your board (To Do → check off = Done). Feel free to convert this into GitHub Issues/Projects later if you want more structure.

---

## Epic 0 — Project Setup
**Goal:** A working, empty Maven project with git, ready for TDD.

- [x] Initialize git repository, add `.gitignore` (Java/Maven/IDE)
- [x] Create Maven project structure (`pom.xml`, `src/main/java/turtle`, `src/test/java/turtle`)
- [x] Add JUnit 5 dependency and confirm `mvn test` runs (even with zero tests)
- [x] Add a placeholder `README.md` describing the project
- [x] Confirm build works: `mvn -q compile` and `mvn -q test`

**Acceptance criteria:** `mvn test` runs successfully with no source files beyond scaffolding.

---

## Epic 1 — Core Turtle State Model (headless, no GUI yet)
**Goal:** A `Turtle` class that tracks position/heading/pen state as plain data + logic, fully unit-testable without opening any window. This mirrors the Python turtle's internal state machine.

Candidate stories (map to Python turtle methods):
- [x] **Story 1.1** — Turtle starts at origin `(0,0)` facing heading `0` (east), pen down, default color/width.
- [x] **Story 1.2** — `forward(distance)` / `backward(distance)` update position based on heading.
- [x] **Story 1.3** — `right(angle)` / `left(angle)` update heading (with proper wraparound at 360°).
- [x] **Story 1.4** — `penUp()` / `penDown()` toggle whether movement draws a line.
- [x] **Story 1.5** — `goTo(x, y)` / `setHeading(angle)` teleport-style updates.
- [x] **Story 1.6** — Track a **path/history** of line segments drawn (for later rendering) — e.g. a list of `(from, to, color, width)` segments, only recorded when pen is down.
- [x] **Story 1.7** — `home()` resets to origin (without clearing drawing, matching Python semantics). **Note:** does not reset heading.

**Acceptance criteria:** Each story has JUnit tests covering normal cases + edge cases (e.g., negative distance, angle > 360, angle < 0). No AWT/Swing classes involved yet.

**Status:** ✅ Complete — 19 unit tests across `TestTurtle` (6), `TestPen` (6), `TestLineSegment` (4), `TestVector2D` (3); all passing. No AWT/Swing code in production classes other than `java.awt.Color` (used as a value type).

---

## Sprint 1 Review — Epic 1 (Core Turtle State Model)

**Demo:** The headless model can be exercised directly in a test or a `main()`. A square walk looks like:
```java
Turtle t = new Turtle();
for (int i = 0; i < 4; i++) { t.forward(100); t.right(90); }
// t.getSegments() → 4 LineSegments forming a closed square
```
All 7 stories completed; `mvn test` green with 19 passing tests.

**What went well:**
- Keeping `Turtle` purely headless made every story trivially unit-testable from the start.
- Extracting `Pen` as its own class (rather than fields on `Turtle`) paid off immediately — pen tests are isolated and the `equals`/`hashCode` on `Pen` made assertions clean.
- Immutable `Vector2D` and `LineSegment` removed a whole class of aliasing bugs (no defensive copies needed anywhere).
- Funnelling all movement through one private `goTo(Vector2D)` kept segment-recording logic in one place.

**What was tricky:**
- Heading normalisation: `((h % 360) + 360) % 360` is non-obvious; worth a comment explaining why the double-mod is needed for negative angles.
- `setHeading` deliberately not normalising (to match Python `setheading` semantics) is an invisible gotcha — easy to forget later. Captured in Decisions Log.
- `home()` not resetting heading (also matching Python) was confirmed late; needed to update the story description.
- `java.awt.Color` is the only AWT import in the model layer — fine as a value type, but worth watching if we ever want a truly headless/non-GUI JAR.

**What to change next sprint:**
- ✅ Write the `Screen` design decision (snapshot vs. live reference) *before* starting Story 2.1: decided on live `Turtle` reference (see Decisions Log).
- Consider a `@BeforeEach` fixture in `TestTurtle` — several tests reconstruct the same initial state manually.

---

## Epic 2 — Rendering the Turtle's Path (Swing window)
**Goal:** Visualize the recorded path from Epic 1 in a real window.

- [x] **Story 2.1** — A `TurtleScreen`/`Window` class that opens a `JFrame` with a custom `JPanel` canvas.
- [x] **Story 2.2** — Canvas paints all recorded line segments from a `Turtle`'s history via `paintComponent`/`Graphics2D`.
- [x] **Story 2.3** — Coordinate system translation: turtle's Cartesian (0,0 = center, y-up) → Swing's pixel coords (0,0 = top-left, y-down).
- [x] **Story 2.4** — Manual demo/smoke test: draw a square, a triangle, a star — visually confirm correctness (not a unit test, a "does it look right" check).

**Acceptance criteria:** Running a small demo `main()` opens a window and draws a shape matching what the equivalent Python turtle script would produce.

**Status:** ✅ Complete — 11 Swing tests are green, including frame construction, canvas ownership, coordinate mapping, color/width rendering, and pen-up exclusion. Full `mvn test` is passing.

### Sprint 2 Review — Epic 2 (Rendering)

**Demo:** A small `TurtleDemo` can create a turtle, walk a square or triangle, show the window, and confirm the drawing appears centered and correctly oriented with the y-axis flipped for Swing.

**What went well:**
- Keeping the rendering layer separate from the headless turtle model made the test strategy straightforward.
- The live-reference design (`Screen` holds the same `Turtle` the canvas reads from) kept the API minimal and fits the eventual animation story.
- Rendering tests based on `BufferedImage` pixels were a reliable way to validate line placement, colors, and width without relying on fragile visual assertions.
- Isolating the coordinate transform inside `TurtleCanvas` kept the business logic independent from Swing details.

**What was tricky:**
- The most common bug was a wrong y-axis conversion: `Swing` uses y-down while the turtle model uses y-up, so the first fix often painted positive y in the wrong direction.
- `Screen` originally tried to be a `JFrame` subclass, which caused recursive `show()`/`setVisible()` calls. Splitting the ownership model into `Screen` + inner `JFrame` resolved that cleanly.
- Headless CI environments require GUI tests to skip themselves; Story 2.1/2.2/2.3 tests should be guarded with `GraphicsEnvironment.isHeadless()`.

**What to change next sprint:**
- Move to Epic 3 (Pen Styling) and add `Turtle` convenience methods for color and width forwarding.
- Consider whether the screen should later support a `title()`, `bgcolor()`, or `setup(width, height)` API as part of a polish pass.

---

## Epic 3 — Pen Styling
**Goal:** Match Python's pen customization API.

- [x] **Story 3.1** — `penColor(color)` on `Turtle` — support named colors and/or RGB. **Note:** `Pen.setColor(Color)` already exists; this story adds the `Turtle`-level forwarding method and a `String`-based color name API to match Python's `turtle.pencolor("red")` syntax.
- [x] **Story 3.2** — `penWidth(width)` on `Turtle`. **Note:** `Pen.setWidth(double)` (with positive-value validation) already exists; this story adds the `Turtle`-level forwarding method.

**Status:** Complete for the current scope. `Turtle` exposes color and width convenience methods, and `LineSegment` captures both values when movement is recorded. Named color parsing remains a future API enhancement if needed.

### Sprint 3 Review — Epic 3 (Pen Styling)

**What went well:**
- Keeping color and width state in `Pen` allowed the `Turtle` methods to remain small forwarding methods.
- Capturing pen style in immutable `LineSegment` values preserved the appearance of earlier movement after later style changes.
- Headless tests covered state changes, segment metadata, and validation without involving Swing.

**What was tricky:**
- A test initially inspected the first segment twice instead of checking the second segment after a color change.
- Formatting cleanup was needed after adding the new tests and API comments.

**What to change next sprint:**
- Keep polygon fill state separate from `LineSegment`; filling needs ordered vertices, a fill color, and completion semantics.
- Add the fill model and tests before adding any Swing polygon rendering.

---

## Epic 4 — Shape Filling
**Goal:** Record and render filled polygons while keeping fill state in the headless model and coordinate conversion in the renderer.

- [ ] **Story 4.1** — `fillColor(color)` stores a fill color separately from the pen color.
- [ ] **Story 4.2** — `beginFill()` starts recording a polygon path from the current turtle position.
- [ ] **Story 4.3** — `endFill()` completes the path and publishes a filled polygon; filling does not render before completion.
- [ ] **Story 4.4** — Canvas renders completed polygons before their outlines, using the stored fill color.
- [ ] **Story 4.5** — Filled polygon coordinate mapping and headless `BufferedImage` rendering tests.

**Acceptance criteria:** A closed triangle or other polygon drawn between `beginFill()` and `endFill()` is rendered with the selected fill color, its outline remains visible, pen-up movement contributes to the fill path without creating a line segment, and incomplete or inactive fills do not create visible polygons. The model remains unit-testable without Swing.

**Design boundary:** The model will expose immutable filled-polygon data containing ordered points and the fill color. The renderer will map each point with the existing turtle-to-Swing transform and draw completed polygons with `Graphics2D`. Nested fill calls and paths with fewer than three points must have explicit behavior in the implementation and tests.

## Epic 5 — Animation / Speed
**Goal:** Optional visual animation instead of instant line drawing, matching `speed()` in Python.

- [ ] **Story 5.1** — `speed(level)` setting (0 = instant, 1-10 = slow-to-fast).
- [ ] **Story 5.2** — Incremental redraw/timer-based animation of movement (this is the trickiest part — likely needs its own design discussion on threading with Swing's EDT).

---

## Epic 6 — Polish & Documentation
- [ ] Javadoc pass on all public API
- [ ] `README.md` with usage examples side-by-side with Python equivalents
- [ ] Example programs (square, star, spiral) in `src/main/java` or a `examples`/`demo` module
- [ ] Review test coverage; fill gaps

---

## Stretch / Future Epics (post-v1, not detailed yet)
- Screen configuration: `bgcolor()`, `title()`, `setup(width, height)`
- Multiple turtles on one screen
- Event handling: `onclick`, `onkey`, `listen()`
- Shapes & stamps: `shape()`, `stamp()`
- `write()` (text rendering)
- Undo (`undo()`) via command history

---

## Working Agreement (how you and I collaborate per story)
1. We pick the next unchecked story.
2. I help you clarify acceptance criteria and relevant Python semantics (checking docs if needed).
3. You write the failing test(s) first; I review/discuss them with you.
4. You implement; I review, point out issues, suggest but don't write the bulk of the solution.
5. We run tests, check the box, move to the next story.

**Next step:** Start Epic 4 — Shape Filling. Epics 0, 1, 2, and 3 are complete for their current scopes. The headless model and Swing renderer are implemented and tested. The design for `Screen` (a `JFrame` + custom `JPanel` that reads `Turtle.getSegments()` and paints via `Graphics2D`) is captured in `docs/design.md`, and the live `Turtle` reference design is recorded in the Decisions Log.

## Retrospective

**What worked well:**
- Breaking the work into headless model, then renderer, then coordinate mapping kept the test surface clean and reduced the chance of mixing concerns.
- Pixel-based tests were effective for validating rendering without needing to inspect a real window manually.
- Keeping `LineSegment` immutable and storing color/width at draw time made the painting logic deterministic and easy to test.

**What we would improve next time:**
- Add GUI tests with stricter EDT discipline from the start, rather than revisiting thread ownership after the fact.
- Separate headless-safe unit tests from display-dependent tests more explicitly in the naming and assumptions.
- Keep the design document and plan in sync as features land, instead of updating them as a follow-up step.
