#ph-pdf-layout

Java library for creating fluid page layouts with Apache PDFBox.

Please check the test files to see how to create PDFs with the different elements.
Version starting with 2.1.0 uses PDFBox 2.x, previous versions (up to and including 2.0.0) use PDFBox 1.8.x.

Note: version 4.0.0 has troubles building with JDK 1.8.0_92 - updating to 1.8.0_112 or later should work.

#News
  * v4.0.0 Beta 4 - 2017-01-10
    * Block elements use full width now by default
    * Improved placeholder handling in text preparation
  * v4.0.0 Beta 3 - 2017-01-10
    * Binds to ph-commons 8.6.x
    * Fixed a height problem with vertical split HBoxes
    * Simplified class hierarchy for table rows
    * Made font fallback code point more flexible
    * Changed font rendering to use descent from font instead of heuristics
    * Fixed different border color rendering
    * Made debug rendering customizable
    * Added support for line spacing in PLText
  * v4.0.0 Beta 2 - 2017-01-03
    * The Maven artifact name was changed to 'ph-pdf-layout4' so that it can be used side-by-side with version 3.
    * The global package name was changed from `com.helger.pdflayout` to `com.helger.pdflayout4` so that both 3.x and 4.x can run side-by-side
    * This is major rewrite to be closer to the CSS box model
    * VBox and HBox have no more layout information assigned to them
    * Added a new element "Box" that allows for easy alignment etc.
    * Separation between renderable objects, block element (box) and inline elements (text and image)
    * New class design for tables, so that each table cell is automatically represented by a box, each table row is a separate object
    * Added a simple grid system for tables to build the default grids easily
    * Added new "auto" width/height for columns/rows
    * Updated to PDFBox 2.0.4
  * v3.5.2 - 2017-01-10
    * Binds to ph-commons 8.6.x
    * Updated to PDFBox 2.0.4
  * v3.5.1 - 2016-10-07
    * Fixed a rendering flaw with borders
  * v3.5.0 - 2016-09-21
    * Changed internal class hierarchy to prepare for future changes
    * Changed package assignments for better grouping
  * v3.0.3 - 2016-09-19
    * Updated to PDFBox 2.0.3
    * Performance improvement by using optimized writer
    * Included optional MicroTypeConverters
  * v3.0.2 - 2016-09-06
    * API extensions for the classes in the "spec" package
  * v3.0.1 - never released because of issues with the release batch file :(   
  * v3.0.0 - 2016-08-21
    * Requires JDK 8
    * Still on PDFBox 2.0.0 because of problems with 2.0.1 and 2.0.2

#Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-pdf-layout</artifactId>
  <version>3.5.2</version>
</dependency>
```
or
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-pdf-layout4</artifactId>
  <version>4.0.0-b3</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodeingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
