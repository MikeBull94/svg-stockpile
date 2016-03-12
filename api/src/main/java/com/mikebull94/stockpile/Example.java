package com.mikebull94.stockpile;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mikebull94.stockpile.svg.SvgDocument;
import com.mikebull94.stockpile.svg.SvgViewBox;
import com.mikebull94.stockpile.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An example use case of the SVG {@link Stockpile} configured to stack and optimize SVG documents.
 */
public final class Example {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Example.class);

	/**
	 * The expected program arguments format.
	 */
	private static final String EXPECTED_ARGUMENTS =
		"Expecting program arguments: <inputDir> <output> <viewBoxMinX> <viewBoxMinY> <viewBoxWidth> <viewBoxHeight>";

	/**
	 * The entry point of the program.
	 * @param args The program's arguments.
	 */
	public static void main(String... args) {
		try {
			logger.info("Starting SVG Stockpile...");

			Preconditions.checkArgument(args.length == 6, EXPECTED_ARGUMENTS);

			Path inputDir = Paths.get(args[0]);
			Path output = Paths.get(args[1]);
			int minX = Integer.parseInt(args[2]);
			int minY = Integer.parseInt(args[3]);
			int width = Integer.parseInt(args[4]);
			int height = Integer.parseInt(args[5]);

			SvgViewBox viewBox = new SvgViewBox(minX, minY, width, height);
			logger.info("Providing embedded SVGs with: {}", viewBox);

			ImmutableList<Path> input = PathUtils.filterPathsIn(inputDir, PathUtils::hasSvgExtension);
			logger.info("Found {} SVG files in: {}", input.size(), inputDir);

			Stockpile stockpile = Stockpile.builder()
				.add(SvgDocument.startSvg(viewBox))
				.add(SvgDocument.hideEmbeddedSvgs())
				.read(input)
				.add(SvgDocument.endSvg())
				.build();

			stockpile.write(output);
			logger.info("Stockpiled {} XML events into: {}", stockpile.size(), output);
		} catch (Throwable t) {
			logger.error("Failed to run SVG Stockpile.", t);
		}
	}

	private Example() {
		/* empty */
	}
}
