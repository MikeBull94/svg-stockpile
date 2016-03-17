package com.mikebull94.stockpile.gradle;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mikebull94.stockpile.Stockpile;
import com.mikebull94.stockpile.svg.SvgViewBox;
import com.mikebull94.stockpile.util.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.mikebull94.stockpile.svg.SvgDocument.endSvg;
import static com.mikebull94.stockpile.svg.SvgDocument.hideEmbeddedSvgs;
import static com.mikebull94.stockpile.svg.SvgDocument.startSvg;

/**
 * A {@link Task} that will read SVG documents from an {@link InputDirectory} into a {@link Stockpile}, then write the
 * {@link Stockpile} to an {@link OutputFile}.
 */
public class StockpileTask extends DefaultTask {

	/**
	 * The group for {@link StockpileTask}s.
	 */
	public static final String GROUP = "svg-stockpile";

	/**
	 * The default name of this task.
	 */
	public static final String NAME = "stockpile";

	/**
	 * The default description of this task.
	 */
	public static final String DESCRIPTION = "Stacks and optimizes Scalable Vector Graphics using the svg-stockpile API.";

	/**
	 * The exception message to use if a property value is unspecified.
	 */
	private static final String UNSPECIFIED_VALUE = "No value has been specified for property '%s'.";

	/**
	 * The directory from which to read SVGs into the {@link Stockpile}.
	 */
	@InputDirectory
	private File inputDir;

	/**
	 * The output {@link File} to write the {@link Stockpile} to.
	 */
	@OutputFile
	private File output;

	/**
	 * The {@link SvgViewBox} to provide the {@link Stockpile} with.
	 */
	private SvgViewBox viewBox;

	/**
	 * Creates a new {@link StockpileTask}.
	 */
	public StockpileTask() {
		setGroup(GROUP);
		setDescription(DESCRIPTION);
	}

	/**
	 * Reads SVG documents from the {@link #inputDir} into a {@link Stockpile}, then writes the {@link Stockpile} to the
	 * {@link #output} file.
	 * @throws IOException If an I/O error occurs.
	 * @throws XMLStreamException If an XML error occurs.
	 */
	@TaskAction
	public void run() throws IOException, XMLStreamException {
		Preconditions.checkNotNull(inputDir, String.format(UNSPECIFIED_VALUE, "inputDir"));
		Preconditions.checkNotNull(output, String.format(UNSPECIFIED_VALUE, "output"));
		Preconditions.checkNotNull(viewBox, String.format(UNSPECIFIED_VALUE, "viewBox"));

		getLogger().info("Starting stockpile...");
		getLogger().info("Providing embedded SVGs with: {}", viewBox);

		ImmutableList<Path> input = PathUtils.filterPathsIn(inputDir.toPath(), PathUtils::hasSvgExtension);
		getLogger().info("Found {} SVG files in: {}", input.size(), inputDir);

		Stockpile stockpile = Stockpile.builder()
			.add(startSvg(viewBox))
			.add(hideEmbeddedSvgs())
			.read(input)
			.add(endSvg())
			.build();

		stockpile.write(output);
		getLogger().info("Stockpiled {} XML events into: {}", stockpile.size(), output);
	}

	/**
	 * Gets the input directory.
	 * @return The input directory represented as a {@link File}.
	 */
	public File getInputDir() {
		return inputDir;
	}

	/**
	 * Sets the input directory.
	 * @param inputDir The input directory to set.
	 * @throws NullPointerException If {@code inputDir} is {@code null}.
	 */
	public void setInputDir(File inputDir) {
		this.inputDir = Preconditions.checkNotNull(inputDir);
	}

	/**
	 * Gets the output {@link File}.
	 * @return The output {@link File}.
	 */
	public File getOutput() {
		return output;
	}

	/**
	 * Sets the output {@link File}.
	 * @param output The output {@link File} to set.
	 * @throws NullPointerException If {@code output} is {@code null}.
	 */
	public void setOutput(File output) {
		this.output = Preconditions.checkNotNull(output);
	}

	/**
	 * Gets the {@link SvgViewBox}.
	 * @return The {@link SvgViewBox}.
	 */
	public SvgViewBox getViewBox() {
		return viewBox;
	}

	/**
	 * Sets the {@link SvgViewBox}.
	 * @param viewBox The {@link SvgViewBox} to set.
	 * @throws NullPointerException If {@code viewBox} is {@code null}.
	 */
	public void setViewBox(SvgViewBox viewBox) {
		this.viewBox = Preconditions.checkNotNull(viewBox);
	}
}
