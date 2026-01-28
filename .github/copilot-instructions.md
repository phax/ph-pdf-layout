# Copilot Instructions for ph-pdf-layout

This repository is a Java library for creating fluid page layouts with Apache PDFBox 3.x.
It uses a CSS-like box model to abstract the low-level PDF primitives.

## Architectural Overview

The core paradigm is a hierarchical box model where elements are composed to form pages.

### Key Components

*   **`PageLayoutPDF`**: The main entry point and container for the entire document. It holds metadata and a list of `PLPageSet`s.
*   **`PLPageSet`**: Represents a group of pages with the same properties (page size, orientation, margin). Elements added to a `PLPageSet` flow across multiple pages if necessary.
*   **Elements (`com.helger.pdflayout.element`)**: The building blocks of the layout.
    *   **Containers**: `PLBox` (general container), `PLHBox` (horizontal row), `PLVBox` (vertical stack), `PLTable`.
    *   **Content**: `PLText` (text with font/styles), `PLImage` (bitmap images), `PLBulletPointList`.
    *   **Structure**: `PLSpacerX`, `PLSpacerY`, `PLPageBreak`.
*   **Styling**: Elements support CSS-like properties:
    *   `margin` (outer spacing)
    *   `border` (visual boundary)
    *   `padding` (inner spacing)
    *   `minSize` / `maxSize`
    *   `fillColor` (background)

### Coordinate System & Layout

*   The library abstracts PDF's native bottom-left origin.
*   Layout is generally **top-down, left-to-right** (like HTML/CSS), handled by `PLVBox` and `PLHBox`.
*   You rarely calculate absolute coordinates manually. Instead, you nest boxes and configure alignments (`EHorzAlignment`, `EVertAlignment`).

## Developer Workflow

### Dependencies & Build

*   **Build System**: Maven (`pom.xml`).
*   **Key Dependencies**:
    *   `org.apache.pdfbox:pdfbox` (v3.x)
    *   `com.helger:ph-commons` (General utility library found throughout the codebase)
    *   `com.helger:ph-fonts` (Font handling)

### Testing & Visual Verification

*   Tests are the primary way to verify layout logic.
*   **Mechanism**:
    1.  Tests generate PDF files into `ph-pdf-layout/pdf/`.
    2.  `PDFTestComparer.renderAndCompare(layout, targetFile)` renders the layout and compares the output pixel-by-pixel (or internally structure-wise) against a reference file.
    3.  **Reference Files**: Stored in `example-files/`.
*   **Workflow for changes**:
    *   If you intentionally modify layout logic, the test *will fail* because the output differs from the reference.
    *   Visually verify the validitity of the new PDF generated in `ph-pdf-layout/pdf/...`.
    *   If correct, **overwrite** the corresponding file in `example-files/` with the new version to update the baseline.

## Coding Conventions

*   **Prefix**: Most public classes start with `PL` (e.g., `PLBox`, `PLText`).
*   **Fluent API**: Setters almost always return `this` to allow method chaining (e.g., `new PLBox().setBorder(...).setPadding(...)`).
*   **Nullability**: The project uses `org.jspecify.annotations` (`@Nullable`, `@NonNull`).
*   **Collections**: Prefers `com.helger.commons.collection` types (e.g., `CommonsArrayList`) over standard JDK collections for additional utility methods.
*   **Text Handling**: `PLText` handles newlines (`\n`) automatically but requires explicit font specification via `FontSpec`.

## Common Implementation Patterns

### Creating a Simple Document

```java
PageLayoutPDF doc = new PageLayoutPDF();
PLPageSet pageSet = new PLPageSet(PDRectangle.A4);

// Add a title
pageSet.addElement(new PLText("Document Title", new FontSpec(PreloadFont.REGULAR, 20))
    .setHorzAlign(EHorzAlignment.CENTER)
    .setPaddingBottom(10));

// Add a content box with border
PLBox content = new PLBox();
content.setBorder(PLColor.BLACK);
content.setPadding(5);
content.addElement(new PLText("Content goes here...", new FontSpec(PreloadFont.REGULAR, 12)));
pageSet.addElement(content);

doc.addPageSet(pageSet);
doc.renderTo(new File("output.pdf"));
```

### Table Layout (Using Box Model)

Tables are often constructed explicitly or using `PLTable`:

```java
PLTable table = new PLTable();
table.setHeaderRowCount(1);

// Header Row
PLTableRow header = new PLTableRow();
header.addCell(new PLTableCell(new PLText("ID", fontBold)));
header.addCell(new PLTableCell(new PLText("Name", fontBold)));
table.addRow(header);

// Data Row
PLTableRow row = new PLTableRow();
row.addCell(new PLTableCell(new PLText("1", fontReg)));
row.addCell(new PLTableCell(new PLText("Alice", fontReg)));
table.addRow(row);
```
