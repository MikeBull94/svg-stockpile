package com.mikebull94.stockpile.gradle;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.mikebull94.stockpile.Stockpile;
import com.mikebull94.stockpile.svg.SvgViewBox;
import com.mikebull94.stockpile.util.PathUtils;
import org.gradle.api.DefaultTask;
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

public class StockpileTask extends DefaultTask {
	@InputDirectory
	private File inputDir;

	@OutputFile
	private File output;

	private SvgViewBox viewBox;

	public StockpileTask() {
		setGroup(StockpilePlugin.GROUP);
		setDescription("Stacks and optimizes Scalable Vector Graphics using the svg-stockpile API.");
	}

	@TaskAction
	public void stackSvgs() throws IOException, XMLStreamException {
		Preconditions.checkNotNull(inputDir, "Please define the input directory.");
		Preconditions.checkNotNull(output, "Please define the output file.");
		Preconditions.checkNotNull(viewBox, "Please define the SVG viewBox.");

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

	public File getInputDir() {
		return inputDir;
	}

	public void setInputDir(File inputDir) {
		this.inputDir = Preconditions.checkNotNull(inputDir);
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = Preconditions.checkNotNull(output);
	}

	public SvgViewBox getViewBox() {
		return viewBox;
	}

	public void setViewBox(SvgViewBox viewBox) {
		this.viewBox = Preconditions.checkNotNull(viewBox);
	}
}
