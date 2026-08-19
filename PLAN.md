# Java Turtle Graphics Library — Project Plan

## Overview
A Java library that replicates the core motion & drawing behavior of Python's [`turtle`](https://docs.python.org/3/library/turtle.html) module. Rendering is done via Java Swing/Java2D. Built with Maven, targeting Java 21, tested with JUnit 5.

## Decisions Log
| Decision | Choice |
|---|---|
| Build tool | Maven |
| Java version | 21 (LTS) |
| Rendering | Java Swing / Java2D window |
| Test framework | JUnit 5 |
| Base package | `turtle` |
| API scope (v1) | Core motion & drawing only (movement, heading, pen up/down, color, speed) |
| Version control | Git, initialized locally |
| Turtle mutability | Mutable with getters (`getPosition`, `getHeading`, `getPen`, `getSegments`) |
| Angle units | Degrees externally; converted to radians internally only for trig |
| Heading convention | 0° = east, increases counter-clockwise, normalised to `[0, 360)` |
| Segment list access | `getSegments()` returns an unmodifiable view |

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
- [x] **Story 1.7** — `home()` resets to origin/heading 0 (without necessarily clearing drawing, matching Python semantics — confirm from docs).

**Acceptance criteria:** Each story has JUnit tests covering normal cases + edge cases (e.g., negative distance, angle > 360, angle < 0). No AWT/Swing classes involved yet.

**Design decisions made:** `Turtle` is mutable with getters. Angles are degrees (matching Python default); radians mode is out of scope for v1. `forward(0)` is a no-op — distance == 0 returns early before any segment is recorded.

---

## Epic 2 — Rendering the Turtle's Path (Swing window)
**Goal:** Visualize the recorded path from Epic 1 in a real window.

- [ ] **Story 2.1** — A `TurtleScreen`/`Window` class that opens a `JFrame` with a custom `JPanel` canvas.
- [ ] **Story 2.2** — Canvas paints all recorded line segments from a `Turtle`'s history via `paintComponent`/`Graphics2D`.
- [ ] **Story 2.3** — Coordinate system translation: turtle's Cartesian (0,0 = center, y-up) → Swing's pixel coords (0,0 = top-left, y-down).
- [ ] **Story 2.4** — Manual demo/smoke test: draw a square, a triangle, a star — visually confirm correctness (not a unit test, a "does it look right" check).

**Acceptance criteria:** Running a small demo `main()` opens a window and draws a shape matching what the equivalent Python turtle script would produce.

---

## Epic 3 — Pen Styling
**Goal:** Match Python's pen customization API.

- [ ] **Story 3.1** — `penColor(color)` — support named colors and/or RGB.
- [ ] **Story 3.2** — `penWidth(width)`.
- [ ] **Story 3.3** — `fillColor` + basic `beginFill()`/`endFill()` (stretch — may be its own epic if complex).

---

## Epic 4 — Animation / Speed
**Goal:** Optional visual animation instead of instant line drawing, matching `speed()` in Python.

- [ ] **Story 4.1** — `speed(level)` setting (0 = instant, 1-10 = slow-to-fast).
- [ ] **Story 4.2** — Incremental redraw/timer-based animation of movement (this is the trickiest part — likely needs its own design discussion on threading with Swing's EDT).

---

## Epic 5 — Polish & Documentation
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

**Next step:** Start Epic 0 (project scaffolding). Let me know when you're ready and I'll guide you through creating the Maven structure (without doing it all for you) — or if you'd like me to scaffold the boilerplate (pom.xml, folders) so we can jump into actual library code sooner.
