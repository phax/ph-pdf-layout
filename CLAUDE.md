# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build everything (compile + test + package)
mvn clean verify -pl ph-pdf-layout

# Compile only (no tests)
mvn compile -pl ph-pdf-layout

# Run all tests
mvn test -pl ph-pdf-layout

# Run a single test class
mvn test -pl ph-pdf-layout -Dtest=PLTextTest

# Run a single test method
mvn test -pl ph-pdf-layout -Dtest=PLTextTest#testBasic
```

## Architecture

Java library for creating fluid page layouts with Apache PDFBox 3.x. Uses a CSS-like box model (margin/border/padding) to abstract low-level PDF primitives. Java 17+.

### Document Model

`PageLayoutPDF` (document) -> `PLPageSet` (page group with shared size/margins) -> elements

Elements flow top-down within a page set and automatically split across pages when needed.

### Two-Phase Rendering

1. **Preparation phase**: `PLPageSet.prepareAllPages()` calculates sizes and splits elements across pages via `PreparationContext`/`PreparationContextGlobal`
2. **Rendering phase**: `PLPageSet.renderAllPages()` writes to PDFBox via `PageRenderContext`

Negative intermediate sizes (e.g. `-2.0` for height) are legitimate during preparation when elements with padding/border exceed available space. The commented-out `ValueEnforcer` checks in `SizeSpec` and `PreparationContext` are intentionally disabled.

### Element Hierarchy

- **Block elements** (`AbstractPLBlockElement`): `PLText`, `PLBox`, `PLVBox`, `PLHBox`, `PLTable`, `PLBulletPointList`, `PLImage`
- **Inline elements** (`AbstractPLInlineElement`): `PLExternalLink` (wraps another element as a clickable link)
- **Special**: `PLPageBreak`, `PLSpacerX`, `PLSpacerY`, `PLSpacerXY`

All elements extend `AbstractPLRenderableObject` -> `AbstractPLElement` -> `AbstractPLObject`.

### Font System

`PreloadFont` -> `LoadedFont` (per-document). Fonts are registered in `PreloadFontManager`, lazily loaded into PDFBox `PDFont` instances per document. Standard 14 PDF fonts available via `PreloadFont.REGULAR`, `.BOLD`, etc. Custom fonts via `PreloadFontManager.getOrAddEmbeddingPreloadFont(IFontResource)`.

### XML Serialization

Spec classes (`FontSpec`, `BorderSpec`, `MarginSpec`, etc.) have micro-type converters in `config/xml/` for XML round-tripping via ph-commons microdom. Registration happens in `PDFMicroTypeConverterRegistry`.

### PDFBox Extensions

`org.apache.pdfbox.pdmodel.font.PDFontHelper` and `PDDocumentHelper` are package-private extensions placed in the PDFBox package namespace to access internal APIs.

## Test Patterns

- **Framework**: JUnit 4 with `PLDebugTestRule` (manages debug state per test)
- **PDF comparison**: `PDFTestComparer.renderAndCompare(layout, targetFile)` renders a layout and compares pixel-by-pixel against reference files in `example-files/`
- **Generated PDFs**: Written to `ph-pdf-layout/pdf/` during test runs
- **When layout changes**: Tests fail because output differs from reference. Visually verify the new PDF in `pdf/`, then overwrite the corresponding `example-files/` baseline

## Key Conventions

- Class prefix `PL` for all public layout classes
- Fluent API: all setters return `this` for chaining
- `@Nullable`/`@NonNull` from `org.jspecify.annotations`
- ph-commons collection types (`ICommonsList`, `CommonsArrayList`) for return types; standard Java types for parameters
- Debug logging controlled via `PLDebugLog` and `PLDebugRender` (static flags, off by default)
