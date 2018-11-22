# ph-pdf-layout

Java library for creating fluid page layouts with Apache PDFBox.

Please check the test files to see how to create PDFs with the different elements.

The basic elements provided are:
* PageLayoutPDF - the entry class, having a list of page sets
* PLPageSet - a set of pages that share the same size and orientation and contain a fluid set of elements
* *PL elements* - basic or complex layout elements.
  * Basic (inline) elements are text (Unicode of course) and image (whatever ImageIO can load).
  * Basic (block) element is box. 
  * Layout elements are h-box, v-box, spacer-x, spacer-y, page break
  * The most complex element is a table, which consists of a number of "h-boxes" (rows) which itself consist of a number of "v-boxes" (columns) plus comes with repeating headlines etc.
  * Elements can have min-size, max-size, margin, padding, border and fill-color - if you know CSS you should be familiar with it.

# News and Noteworthy

* v5.0.8 - 2018-11-22
    * Updated to PDFBox 2.0.12
    * Updated to ph-commons 9.2.0
* v5.0.7 - 2018-07-10
    * Updated to PDFBox 2.0.11
* v5.0.6 - 2018-06-21
    * Updated to org.apache.pdfbox:jbig2-imageio for JPEG handling
    * Fixed OSGI ServiceProvider configuration
    * Updated to ph-commons 9.1.2
* v5.0.5 - 2018-04-16
    * Something went wrong when publishing to Maven Central - next try
* v5.0.4 - 2018-04-16
    * Do not justify the last line of multiline text
* v5.0.3 - 2018-04-16
    * Added `ph-collection` dependency for issue #4
    * Updated to PDFBox 2.0.9
    * Added possibility to justify text
* v5.0.2 - 2018-02-21
    * Added possibility to use special page header and footer on the first page of a PLPageSet
* v5.0.1 - 2018-02-12
    * Added image type support (issue #3)
    * Updated to BouncyCastle 1.59
    * Added new table grid types
* v5.0.0 - 2017-11-09
    * Updated to PDFBox 2.0.8
    * Updated to ph-commons 9.0.0
    * Updated to BouncyCastle 1.58
* v4.0.1 - 2017-05-16
    * Updated to PDFBox 2.0.6
    * Slight API extensions
* v4.0.0 - 2017-02-22
    * No change compared to 4.0.0 Beta 5
* v4.0.0 Beta 5 - 2017-01-19
    * Improved XML serialization slightly
    * Fixed an NPE with PLBox without a contained element
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
* v3.5.3 - 2017-11-07
    * Binds to ph-commons 9.0.0
    * Updated to PDFBox 2.0.8
    * Updated to BouncyCastle 1.58
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
* v3.0.1 - never released because of issues with the release script :(   
* v3.0.0 - 2016-08-21
    * Requires JDK 8
    * Still on PDFBox 2.0.0 because of problems with 2.0.1 and 2.0.2

Version starting with 2.1.0 uses PDFBox 2.x, previous versions (up to and including 2.0.0) use PDFBox 1.8.x.
Note: version 3.5.2 is not actively developed - in the meantime ph-pdf-layout 4 is the way forward.
Note: version 4.0.0 has troubles building with JDK 1.8.0_92 - updating to 1.8.0_112 or later should work.

# Maven usage

Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-pdf-layout4</artifactId>
  <version>5.0.8</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
