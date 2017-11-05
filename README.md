#ph-pdf-layout

Java library for creating fluid page layouts with Apache PDFBox.

Please check the test files to see how to create PDFs with the different elements.
Version starting with 2.1.0 uses PDFBox 2.x, previous versions (up to and including 2.0.0) use PDFBox 1.8.x.

#News
  * v3.5.3 - work in progress
    * Binds to ph-commons 9.0.0
    * Updated to PDFBox 2.0.8
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

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodeingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
