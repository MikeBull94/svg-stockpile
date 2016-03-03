# svg4j

Svg4j is an optimizing and stacking tool for [Scalable Vector Graphics][svg].

With SVGs being [heavily supported][caniuse-svg] on the web many may wish to
compress and package all of their SVG assets into a single optimized file. This
technique is known as using a [sprite-sheet][spritesheet]. This can be achieved
in SVG documents with the use of [SVG fragment identifiers][svg-fragments], a
method of rendering part of an SVG by defining a view ID and referring to it in
an `<img>` tag. This technique is [fairly supported][caniuse-svg-fragment] by
most modern browsers.

## Usage

An example of the tool's usage is provided in the
[`Svg4j`](core/src/main/java/com/mikebull94/svg4j/Svg4j.java) class. The example
searches in [`core/src/main/resources`](core/src/main/resources) for files with
the `.svg` extension, stacking and optimizing them into
`core/build/resources/main`. This directory also contains an
[`example.html`](core/src/main/resources/example.html) file that shows how to
refer to the embedded SVG elements by their fragment identifier.

The example SVGs included in the distribution are variations of the [Wikimedia
Community Logo][wikimedia-community-logo], licensed for any purpose within the
public domain.

```java
Svg4j svg4j = new Svg4j();
SvgViewBox viewBox = new SvgViewBox(0, 0, 500, 500);
XmlDocument stacked = svg4j.stack(viewBox, Paths.get("input1.svg"), Paths.get("input2.svg"));
Path output = result.write(Paths.get("output.svg"));
```

## Building

[Gradle][gradle] is used as the project's build system. The [Gradle Wrapper]
[gradle-wrapper] is included in the distribution, therefore you do not need to
install Gradle on your system.

To build the program and generate aggregated Javadoc, run:

* `./gradlew` (on Unix-like platforms such as Linux and Mac OS X)
* `gradlew` (on Windows using the [gradlew.bat](gradlew.bat) batch file)

[FindBugs™][findbugs] and [PMD][pmd] are used to [lint][lint] the Java code and
will fail the build on rule violations.

## Documentation

Javadoc can be generated with the `aggregateJavadoc` [Gradle Task][gradle-task].

To generate the latest Javadoc (stored at `build/docs/javadoc`), run:

* `./gradlew aggregateJavadoc` (on Unix-like platforms such as Linux and Mac OS
X)
* `gradlew aggregateJavadoc` (on Windows using the [gradlew.bat](gradlew.bat)
batch file)

## Dependencies

* Java 8 or above
* [Google Guava][guava]
* [SLF4J][slf4j] (for logging)
* [JUnit][junit] (for unit tests)
* [APIviz][apiviz] (for UML-like class diagrams in the generated Javadoc)
* [FindBugs™][findbugs] (for Java code linting)
* [PMD][pmd] (for Java code linting)

## License

This project is available under the terms of the ISC license. See the
[`LICENSE`](LICENSE) file for the copyright information and licensing terms.

[svg]: https://www.w3.org/Graphics/SVG/
[caniuse-svg]: http://caniuse.com/#feat=svg
[spritesheet]: https://css-tricks.com/css-sprites/
[svg-fragments]: https://css-tricks.com/svg-fragment-identifiers-work/
[caniuse-svg-fragment]: http://caniuse.com/#feat=svg-fragment
[wikimedia-community-logo]: https://commons.wikimedia.org/wiki/File:Wikimedia_Community_Logo.svg
[gradle]: https://gradle.org/
[gradle-wrapper]: https://docs.gradle.org/current/userguide/gradle_wrapper.html
[gradle-task]: https://docs.gradle.org/current/dsl/org.gradle.api.Task.html
[findbugs]: http://findbugs.sourceforge.net/
[pmd]: https://pmd.github.io/
[lint]: https://en.wikipedia.org/wiki/Lint_%28software%29
[guava]: https://github.com/google/guava
[slf4j]: http://slf4j.org/
[junit]: http://junit.org/
[apiviz]: https://github.com/grahamedgecombe/apiviz
