# ph-pdf-layout

[![javadoc](https://javadoc.io/badge2/com.helger/ph-pdf-layout/javadoc.svg)](https://javadoc.io/doc/com.helger/ph-pdf-layout)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.helger/ph-pdf-layout/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.helger/ph-pdf-layout) 

Java library for creating fluid page layouts with Apache PDFBox.

Please check the test files to see how to create PDFs with the different elements.

The basic elements provided are:
* `PageLayoutPDF` - the entry class, having a list of page sets
* `PLPageSet` - a set of pages that share the same size and orientation and contain a set of elements. The assignments to pages happens dynamically.
* *PL elements* - basic or complex layout elements ("PL" is short for "PDF Layout")
  * Basic (inline) elements are
     * plain text in class `PLText` (Unicode of course)
       * For custom Open Source fonts to be used see the https://github.com/phax/ph-fonts project
       * Note: the available characters heavily depend on the used font. So if you get a "?" character, try loading a different font
     * and image in classes `PLImage` and `PLStreamImage` (whatever ImageIO can load).
  * Basic (block) element is box (class `PLBox`)
  * Layout elements are
    * horizontal box or h-box in class `PLHBox` - like a row of a table
    * vertical box or v-box in class `PLVBox` - like a column of a table
    * spacer-x in class `PLSpacerX` - a horizontal spacer - just in case you need explicit distance to a certain element
    * spacer-y in class `PLSpacerY` - a vertical spacer - just in case you need explicit distance to a certain element
    * page break in class `PLPageBreak` - an explicit page break that starts a new page
  * The most complex element is a table, which consists of a number of "h-boxes" (rows) which itself consist of a number of "v-boxes" (columns) plus comes with repeating headlines etc.
    * See classes `PLTable`, `PLTableRow` and `PLTableCell` for details
  * Elements can have the following properties - if you know CSS you should be familiar with it:
    * "min-size" - the minimum element size
    * "max-size" - the maximum element size
    * "margin" - a transparent outer border (outside of the border)
    * "border" - a visible border with different styles (between padding and margin) 
    * "padding" - a transparent inner border (inside of the border)
    * "fill-color" - the background or fill color of an element

A set of example files as created from the unit test can be found in folder [example-files](https://github.com/phax/ph-pdf-layout/tree/master/example-files). The source code for these examples is https://github.com/phax/ph-pdf-layout/tree/master/src/test/java/com/helger/pdflayout

Similar libraries in this context:
* https://github.com/ralfstuckert/pdfbox-layout - seems to focus more on text layouting

# Maven usage

Add the following to your pom.xml to use this artifact, replacing `x.y.z` with the real version number:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-pdf-layout4</artifactId>
  <version>x.y.z</version>
</dependency>
```

Between v4.0.0 and v5.2.2 the `artifactId` was called `ph-pdf-layout4`

# News and Noteworthy

* v7.1.0 - 2023-08-20
    * Updated to PDFBox 3.0.0
* v7.0.1 - 2023-07-31
    * Updated to PDFBox 2.0.29
    * Updated to ph-commons 11.1.0
    * Improved API to create an empty cell. See [issue #29](https://github.com/phax/ph-pdf-layout/issues/29) - thx @fheldt
* v7.0.0 - 2022-09-14
    * Using Java 11 as the baseline
    * Updated to ph-commons 11
* v6.0.3 - 2022-08-17
    * Added support for PDF/A creation in `PageLayoutPDF` - thx @robertholfeld for publishing this in his branch
* v6.0.2 - 2022-05-25
    * Extended `PDPageContentStreamWithCache` API. See [issue #23](https://github.com/phax/ph-pdf-layout/issues/23) - thx @schneidh
* v6.0.1 - 2022-05-07
    * Updated to jbig2-imageio 3.0.4
    * Updated to PDFBox 2.0.26
    * Added support for creating external links via new class `PLExternalLink`. See [issue #14](https://github.com/phax/ph-pdf-layout/issues/14) - thx @rgarg-atheer and @martin19
* v6.0.0 - 2022-01-05
    * Changed artifactId from `ph-pdf-layout4` to `ph-pdf-layout`
    * Changed Java namespaces `com.helger.pdflayout4.*` to `com.helger.pdflayout.*`
* v5.2.2 - 2021-12-29
    * Updated to PDFBox 2.0.25
    * Extended `IPLHasMargin` with `(set|add)Margin(X|Y)` to set or add to vertical or horizontal margin at once
    * Extended `IPLHasPadding` with `(set|add)Padding(X|Y)` to set or add to vertical or horizontal padding at once
* v5.2.1 - 2021-03-22
    * Updated to PDFBox 2.0.23
* v5.2.0 - 2021-03-21
    * Updated to ph-commons 10
    * Updated to PDFBox 2.0.22
    * Add syntactic sugar method `PLTableCell.createEmptyCell()`
* v5.1.2 - 2020-06-15
    * Updated to PDFBox 2.0.20
    * Allow different page content height if the first page header and footer have different heights. See [issue #14](https://github.com/phax/ph-pdf-layout/issues/14).
* v5.1.1 - 2020-05-29
    * Updated to ph-fonts 4.1.0 (changed Maven groupId)
* v5.1.0 - 2020-03-29
    * Updated to PDFBox 2.0.19
    * Updated to jbig2-imageio 3.0.3
    * Fixed line spacing on page break (see [issue #10](https://github.com/phax/ph-pdf-layout/issues/10))
    * Allow table columns with different `WidthSpec` types, as long as colspan is `1`.
    * Added another generic parameter to `IPLSplittableObject`
    * Made `PageLayoutPDF` API more chainable
    * New class `PLBulletPointList` can be used to create regular bullet point lists (see [issue #9](https://github.com/phax/ph-pdf-layout/issues/9))
    * Updated to ph-commons 9.4.0
* v5.0.9 - 2019-04-29
    * Updated to PDFBox 2.0.15 (security update)
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
Note: version 4.0.0 has troubles building with JDK 1.8.0_92 - updating to 1.8.0_112 or later should work.

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.
