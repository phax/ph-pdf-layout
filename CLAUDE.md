# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`ph-pdf-layout` is a Java library for creating fluid page layouts with Apache PDFBox 3.x. It abstracts low-level PDF primitives using a CSS-like box model (margin/border/padding/fill-color) where elements are composed hierarchically and flow across pages dynamically.

Requires Java 17+.

## Build & Test Commands

```bash
# Full build
mvn clean install

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=PLTextTest

# Run a specific test method
mvn test -Dtest=PLTextTest#testBasic
```

## Testing & Visual Verification Workflow

Tests generate PDFs into `ph-pdf-layout/pdf/` and compare them pixel-by-pixel against reference files in `example-files/` using `PDFTestComparer.renderAndCompare(layout, targetFile)`.

**When intentionally changing layout logic**: The test will fail because output differs from the reference. Visually inspect the newly generated PDF in `ph-pdf-layout/pdf/`, then overwrite the corresponding baseline in `example-files/` to update it.

## Architecture

### Entry Points & Core Hierarchy

```
PageLayoutPDF          ← document root, holds metadata + list of PLPageSets
  └── PLPageSet        ← pages with same size/orientation/margin; elements flow across pages dynamically
        ├── header/footer (IPLSplittableObject)
        └── elements (IPLRenderableObject)
              ├── PLBox         — general container block
              ├── PLHBox        — horizontal row layout
              ├── PLVBox        — vertical stack layout
              ├── PLTable       — complex table with repeating headers (PLTableRow → PLTableCell)
              ├── PLText        — text with FontSpec
              ├── PLImage / PLStreamImage
              ├── PLBulletPointList
              ├── PLSpacerX / PLSpacerY / PLSpacerXY
              ├── PLPageBreak
              └── PLExternalLink
```

### Package Structure

- `com.helger.pdflayout` — `PageLayoutPDF` entry point
- `base/` — interfaces and abstract base classes for the element hierarchy (`AbstractPLElement`, `IPLRenderableObject`, `IPLSplittableObject`, etc.)
- `element/` — concrete layout elements, organized by type (`box/`, `hbox/`, `vbox/`, `text/`, `image/`, `table/`, `list/`, `link/`, `special/`)
- `spec/` — immutable value objects for styling: `FontSpec`, `BorderSpec`, `MarginSpec`, `PaddingSpec`, `WidthSpec`, `HeightSpec`, `SizeSpec`, `EPLRotate`, `EHorzAlignment`, `EVertAlignment`
- `render/` — rendering and layout calculation pipeline (`PageRenderContext`, `PagePreRenderContext`, `PreparationContext`, `PLRenderHelper`)
- `pdfbox/` — PDFBox extensions (`PDPageContentStreamWithCache`, `PDDocumentHelper`)
- `config/` — micro-type converters for XML serialization
- `debug/` — debug utilities

### Layout Coordinate System

The library abstracts PDF's native bottom-left origin. Layout flows **top-down, left-to-right** (like HTML/CSS) via `PLVBox`/`PLHBox`. Absolute coordinates are rarely needed directly.

Internally the library uses **PDF native coordinates** (y increases upward from the page bottom). `PageRenderContext.getStartTop()` is the **top edge y-coordinate** of an element in PDF space (larger y = higher on page). Going "down" the page means decreasing y, which is why you see patterns like `fBottom = fTop - fHeight` in render helpers.

### PDFBox Rendering: Two Separate Coordinate Spaces

PDFBox has two independent coordinate systems that must be handled separately when implementing rotation or any transform:

1. **Graphics space** — controlled by the Current Transformation Matrix (CTM). All path drawing, images, and clipping use this. Modified with `PDPageContentStream.transform(Matrix)`.

2. **Text space** — controlled by the text matrix (Tm), set with `setTextMatrix()` / `newLineAtOffset()` / `moveTextPositionByAmount()`. Text operators (`BT`/`ET` blocks) use text space, which is **independent of the CTM**. Applying `transform()` outside a text block does not affect text positioned via `moveTextPositionByAmount` inside a subsequent `BT`/`ET` block — the text matrix must be set explicitly.

This means **a CTM-only rotation does not rotate text**. Rotating text elements requires setting the text matrix directly inside the `BT`/`ET` block to incorporate the rotation, rather than relying on an outer `transform()` call.

### PDFBox CTM Transform Convention

`PDPageContentStream.transform(M)` pre-multiplies the CTM: `CTM_new = M × CTM_old`.

When multiple `transform()` calls are chained, **the first call is applied first to user-space points** (rightmost in matrix multiplication):

```
transform(M1); transform(M2); transform(M3);
// Effective: P_device = M3 × M2 × M1 × P_user
```

To achieve an effective transform of `T(tx,ty) × R(angle)` (rotate then translate), call in order:

```java
cs.transform(Matrix.getRotateInstance(Math.toRadians(angle), 0, 0)); // M1
cs.transform(Matrix.getTranslateInstance(tx, ty));                    // M2
```

### Rotation Implementation (`EPLRotate`)

Rotation is implemented in `AbstractPLRenderableObject`. The two-phase approach:

1. **Prepare phase** — for vertical rotations (90°/270°), the available width and height passed to `onPrepare()` are swapped so the element sizes itself for its rotated orientation. `m_aPreparedSize` stores content dimensions in the element's own (pre-rotation) space; `m_aRenderSize` stores the swapped dimensions as they appear on the page.

2. **Render phase** — `onRender()` is called via a transformed graphics context. The correct placement transforms per rotation (where `fX,fY` is the top-left of the allocated box, `fWc,fHc` are the prepared content dimensions):

   | Rotation | Effective transform | PDFBox call order |
   |---|---|---|
   | 90° CW | `T(fX, fY) × R(-90°)` | `transform(R(-90°))`, `transform(T(fX, fY))` |
   | 180° | `T(fX+fWc, fY) × R(180°)` | `transform(R(180°))`, `transform(T(fX+fWc, fY))` |
   | 270° CW | `T(fX+fHc, fY-fWc) × R(+90°)` | `transform(R(+90°))`, `transform(T(fX+fHc, fY-fWc))` |

   The new `PageRenderContext` passed to `onRender` always has `startLeft=0, startTop=fHc, width=fWc, height=fHc` — the element renders in its own unrotated space; the CTM handles the visual rotation.

### Key Design Patterns

- **Fluent API**: Setters return `this` for method chaining — `new PLBox().setBorder(...).setPadding(...).setFillColor(...)`
- **Self-typed generics**: Abstract classes use `<IMPLTYPE extends AbstractPLFoo<IMPLTYPE>>` so fluent setters return the concrete type
- **Capability mixins**: CSS-property interfaces (`IPLHasMargin`, `IPLHasBorder`, `IPLHasPadding`, `IPLHasFillColor`) combined via `IPLHasMarginBorderPadding`
- **Splittable objects**: Elements implement `IPLSplittableObject` to split across page boundaries, returning `PLSplitResult`
- **Immutable specs**: `FontSpec`, `BorderSpec`, etc. are value objects shared across elements

## Coding Conventions

- **Class prefix**: All public layout classes start with `PL` (e.g., `PLBox`, `PLText`)
- **Nullability**: Uses `org.jspecify.annotations` (`@Nullable`, `@NonNull`) throughout
- **Collections**: Prefers `com.helger.commons.collection` types (e.g., `CommonsArrayList`) over JDK collections
- **Fonts**: Always specified via `FontSpec(PreloadFont, size)`. Missing glyphs render as `?` — use an appropriate font
- **Text**: `PLText` handles `\n` automatically
