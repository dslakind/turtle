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
| Coverage | JaCoCo 0.8.15; core library reported separately from graphical demo entry points |
| Base package | `turtle` |
| API scope (v1) | Core motion, pen styling, polygon fills, Swing rendering, and headless image tests |
| Version control | Git, initialized locally |
| Turtle mutability | Mutable with getters (`getPosition`, `getHeading`, `getPen`, `getSegments`) |
| Angle units | Degrees externally; converted to radians internally only for trig |
| Heading convention | 0° = east, increases counter-clockwise, normalised to `[0, 360)` |
| Segment list access | `getSegments()` returns an unmodifiable view |
| `Vector2D` mutability | Immutable — all operations (`add`, etc.) return new instances |
| `LineSegment` mutability | Immutable — all fields `final`; captures pen color & width at draw time |
| `forward(0)` behaviour | No-op — returns before computing position or recording a segment |
| `forward` / `goTo` delegation | Public `goTo(double, double)` and `forward` both delegate to private `goTo(Vector2D)`; all segment-recording logic lives there |
| `speed(level)` boundaries | Numeric values from `0.5` through below `10.5` are rounded to levels `1`–`10`; values below `0.5` or at/above `10.5` become `0` by project choice |
| Animation model timing | Movement methods update the headless model and return immediately; Swing may still be visually animating the resulting movement |
| Animation command ordering | New movement commands are accepted immediately and the renderer reveals recorded movements sequentially in command order |
| Animation cursor ownership | The visible-segment index and progress along the active segment belong to Swing-side controller or canvas state, never to `Turtle` |
| `setHeading` normalisation | Does **not** normalise to `[0, 360)` — only `left`/`right` normalise |
| `home()` heading | Does **not** reset heading — only resets position to origin |
| `Pen.setWidth` validation | Throws `IllegalArgumentException` for zero or negative values |
| `Pen` value equality | Implements `equals`/`hashCode` (by `isDown`, `color`, `width`) to simplify test assertions |
| `Screen` role | Pure renderer — reads live turtle segments and completed polygons and paints; owns no turtle state |
| `Screen` coupling | Holds a **live `Turtle` reference** — each repaint reads current segments and completed polygons; no snapshot passing. |

### Rendering and fill semantics

- Turtle space is Cartesian: `(0, 0)` is the canvas center, positive x points right, and positive y points up.
- `TurtleCanvas` maps vertices with `screenX = width / 2 + x` and `screenY = height / 2 - y`, using actual component dimensions.
- `beginFill()` starts a path at the current position. Every subsequent movement contributes a vertex, including pen-up movement.
- `endFill()` is the point at which a path with at least three points becomes a completed `FilledPolygon`; its fill color is captured then.
- `TurtleCanvas` fills completed polygons before drawing segment outlines, preserving visible outlines.
- Headless tests render through `BufferedImage.TYPE_INT_RGB` and assert interior, exterior, outline, and coordinate-mapping pixels.
- Self-intersecting polygons are subject to Java2D's fill rule. Python documents equivalent overlap behavior as graphics-system dependent, so a star's center is not required to fill uniformly.

Out of scope for v1 (candidates for later epics): screen/window configuration (bgcolor, title, size), shapes/stamps, event handlers (onclick/onkey), multiple simultaneous turtles, undo/clone, text/write().

## How We'll Work (Agile, solo-adapted)
Since there's no team to coordinate, we'll keep the ceremony lightweight but still get the benefits of Agile: small increments, working software early and often, and regular reflection.

- **Backlog**: The epics/stories below are the product backlog, roughly ordered by priority/dependency.
- **Sprints**: Instead of calendar time-boxing, each "sprint" = one epic (or a coherent chunk of one). We finish a sprint when its stories meet their acceptance criteria and tests pass.
- **Definition of Done** for every story:
  1. Code implemented (by you, with my guidance/review — I won't write most of it).
  2. Unit tests written first or alongside (TDD where practical) and passing.
3. `mvn verify` is green and refreshes the JaCoCo report.
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

- [x] **Story 4.1** — `fillColor(color)` stores a fill color separately from the pen color.
- [x] **Story 4.2** — `beginFill()` starts recording a polygon path from the current turtle position.
- [x] **Story 4.3** — `endFill()` completes the path and publishes a filled polygon; filling does not render before completion.
- [x] **Story 4.4** — Canvas renders completed polygons before their outlines, using the stored fill color.
- [x] **Story 4.5** — Filled polygon coordinate mapping and headless `BufferedImage` rendering tests.

**Acceptance criteria:** A closed triangle or other polygon drawn between `beginFill()` and `endFill()` is rendered with the selected fill color, its outline remains visible, pen-up movement contributes to the fill path without creating a line segment, and incomplete or inactive fills do not create visible polygons. The model remains unit-testable without Swing.

**Design boundary:** The model will expose immutable filled-polygon data containing ordered points and the fill color. The renderer will map each point with the existing turtle-to-Swing transform and draw completed polygons with `Graphics2D`. Nested fill calls and paths with fewer than three points must have explicit behavior in the implementation and tests.

## Retrospective — Epic 4 (Shape Filling)

**What worked well:**
- Separating `FilledPolygon` from `LineSegment` kept fill geometry, fill color, and stroke styling independently testable.
- Capturing polygon data at `endFill()` made completed fills immutable and preserved each polygon's color after later turtle state changes.
- Rendering polygons before line segments kept outlines visible without introducing a second outline abstraction.
- Headless `BufferedImage` tests validated actual pixels for fill color, background preservation, outline visibility, pen-up paths, and non-square canvases.
- Checking the Python turtle documentation resolved an ambiguity in fill timing and aligned the implementation with the documented `begin_fill()`/`end_fill()` lifecycle.

**What was tricky:**
- Several initial assertions sampled pixels outside the polygons, which looked like rendering failures even though Java2D had painted the shapes correctly.
- The five-point star exposed the platform-dependent behavior of self-intersecting polygon fills: its center is not guaranteed to fill uniformly.
- A demo placement moved one polygon outside the image bounds, reinforcing that pixel tests must calculate screen coordinates from turtle coordinates before choosing sample points.

**What we would improve next time:**
- Choose test sample points from explicit polygon geometry and document their turtle-to-screen calculation beside each assertion.
- Add a small reusable test helper for rendering and for asserting interior/background pixels without duplicating coordinate reasoning.
- Decide explicitly whether future star or self-intersecting-shape APIs should preserve Java2D's fill rule or provide a custom fill policy.
- Keep the plan, design notes, and retrospective updated in the same pass when an epic closes.

## Epic 5 — Animation / Speed
**Goal:** Optional visual animation instead of instant line drawing, matching `speed()` in Python.

Animation policy: `Turtle` updates final model state immediately and returns.
The Swing renderer owns visual progress and reveals newly recorded movements
in command order. The animation cursor, including the visible-segment index
and progress along the active segment, belongs to Swing-side controller or
canvas state and is not part of `Turtle`. Commands issued while earlier
movements are animating are accepted immediately and queued for visual
playback. Speed `0` skips queue animation and displays the final model state
immediately.

- [x] **Story 5.1** — `speed(level)` setting (0 = instant, 1-10 = slow-to-fast).
- [x] **Story 5.2** — Incremental redraw/timer-based animation of movement using Swing's EDT.

**Status:** Complete — `TurtleCanvas` owns a 16 ms Swing `Timer` and a visual movement cursor, replays recorded pen-down and pen-up movements in command order, and reveals speed-0 movement immediately. Animation, cursor, fill-timing, and speed behavior are covered by the green test suite (104 tests).

## Retrospective — Epic 5 (Animation / Speed)

**What worked well:**
- Keeping model updates immediate while animation state lives in `TurtleCanvas` preserved the headless `Turtle` API and prevented rendering concerns from leaking into the model.
- Recording every movement, including pen-up movement, let the visible turtle position animate correctly without creating unwanted line segments.
- A movement index plus a fractional progress value provided one consistent cursor for lines, turtle position and heading, queued commands, and completed-fill visibility.
- A Swing `Timer` kept animation ticks on the Event Dispatch Thread, and package-private animation hooks made the behavior deterministic in headless tests.
- Speed `0` cleanly bypasses incremental playback while preserving the same final model state.

**What was tricky:**
- The movement list and line-segment list cannot share an index because pen-up movements are recorded but do not create segments.
- Newly completed fills must stay hidden until the movement that completed them is fully visible; otherwise fills appear ahead of their animated outlines.
- The visible turtle needs historical heading and interpolated position rather than the turtle's already-final model state.
- Floating-point progress needs a completion tolerance so animation reliably advances to the next movement.

**What we would improve next time:**
- Replace the current linear speed-to-progress mapping with a documented duration or distance-based timing model if more Python-like pacing is needed.
- Extract repeated rendering and animation setup into test helpers as the headless Swing suite grows.
- Keep story checkboxes, status, and retrospectives current in the same commit that closes an epic.

---

## Epic 6 — Polish & Documentation
- [x] Javadoc pass on all public API
- [x] `README.md` with usage examples and rendering/fill semantics
- [x] Example programs (square, star, spiral) in `src/main/java` or a `examples`/`demo` module
- [x] Review test coverage; fill gaps

**Status:** Complete — a coverage-driven audit added 12 focused contract tests for constructor validation, defensive copies, unmodifiable histories, canvas defaults, and invalid animation progress. The audit also corrected the documented `Pen` constructor rule so zero and negative widths are rejected consistently. All 118 JUnit tests pass. JaCoCo reports 98% instruction coverage and 90% branch coverage for the core library.

### Retrospective — Epic 6 (Polish & Documentation)

**What worked well:**
- Reviewing coverage by behavior kept the work focused on public contracts and renderer invariants rather than an arbitrary percentage.
- The existing headless rendering seams made it easy to verify animation and Swing behavior deterministically.
- Contract tests exposed a real inconsistency between the `Pen` constructor, its setter, and its Javadocs.
- Keeping demo entry points outside the coverage target avoided low-value tests that would only execute sample `main()` methods.

**What was tricky:**
- Aggregate coverage includes graphical demos, so the project-wide percentage understates core-library coverage.
- Immutable value objects contain equality and validation branches that need direct contract tests rather than feature-level rendering tests.

**What we would improve next time:**
- - Enforce the existing JaCoCo report in GitHub Actions and keep the coverage floors realistic as the public API grows.
- Keep defensive-copy, validation, and collection-mutability assertions alongside each new public API from the start.

---

## Epic 7 — Release Preparation & CI
**Goal:** Make the completed v1 library reproducible, automatically verified, documented, and ready for a tagged GitHub release.

- [x] **Story 7.1** — Configure JaCoCo Maven reporting for the core library, excluding graphical demo entry points.
- [x] **Story 7.2** — Add a GitHub Actions workflow that runs `mvn verify` for pushes and pull requests.
- [x] **Story 7.3** — Enforce initial JaCoCo minimums of 95% instruction coverage and 85% branch coverage.
- [x] **Story 7.4** — Complete a final public-API and value-contract audit, including null handling, defensive copies, `equals`, and `hashCode`.
- [x] **Story 7.5** — Add a `CHANGELOG.md`, confirm the project license, and review release metadata.
- [x] **Story 7.6** — Generate and inspect Javadocs and the distributable JAR with a clean `mvn verify` build.
- [ ] **Story 7.7** — Set the release version, tag the stable commit, and create a GitHub release with concise usage and verification notes.

**Acceptance criteria:** GitHub automatically verifies every push and pull request; the build fails below the agreed coverage floors; release documentation and licensing are present; generated Javadocs and the JAR are inspected; and the release commit is tagged and published on GitHub.

**Status:** Stories 7.1–7.6 are complete. A clean Maven verification build passes all 123 tests and coverage checks. The distributable JAR, packaged Javadocs, and browsable API documentation were generated and inspected. Release versioning and publication remain.

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

**Next step:** Begin Story 7.7: set the release version, tag the stable commit, and create the GitHub release with concise usage and verification notes.

## Retrospective

**What worked well:**
- Breaking the work into headless model, then renderer, then coordinate mapping kept the test surface clean and reduced the chance of mixing concerns.
- Pixel-based tests were effective for validating rendering without needing to inspect a real window manually.
- Keeping `LineSegment` immutable and storing color/width at draw time made the painting logic deterministic and easy to test.

**What we would improve next time:**
- Add GUI tests with stricter EDT discipline from the start, rather than revisiting thread ownership after the fact.
- Separate headless-safe unit tests from display-dependent tests more explicitly in the naming and assumptions.
- Keep the design document and plan in sync as features land, instead of updating them as a follow-up step.
