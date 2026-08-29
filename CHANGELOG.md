
# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-08-29

### Added

- A Java 21 turtle state model with movement, heading, pen, fill, and speed APIs.
- Swing/Java2D rendering with centered Cartesian coordinates and a visible turtle cursor.
- Timer-driven animation for pen-down and pen-up movement.
- Headless rendering, animation, fill, cursor, and value-contract tests.
- Graphical demonstration programs under `turtle.demo`.
- Maven packaging, JaCoCo coverage reporting and enforcement, and GitHub Actions verification.

### Changed

- Hardened public API contracts for null values, defensive copies, finite numeric values, and equality/hash-code consistency.
- Documented architecture, rendering behavior, animation semantics, and release planning.

### Fixed

- Kept `Vector2D`, `LineSegment`, `Movement`, `Pen`, and `FilledPolygon` equality and validation contracts internally consistent.
- Ensured completed fills remain hidden until their completing movement is visible during animation.

[Unreleased]: https://github.com/dslakind/turtle/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/dslakind/turtle/releases/tag/v0.1.0
