package com.mikebull94.svg4j.gradle;

import com.google.common.base.Preconditions;
import com.mikebull94.svg4j.Svg4j;
import com.mikebull94.svg4j.svg.SvgViewBox;
import com.mikebull94.svg4j.util.PathUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Svg4jTask extends DefaultTask {
	@InputDirectory
	private File inputDir;

	@OutputFile
	private File output;

	private SvgViewBox viewBox;

	public Svg4jTask() {
		setGroup(Svg4jPlugin.GROUP);
		setDescription("Stacks Scalable Vector Graphics using the svg4j API.");
	}

	@TaskAction
	public void stackSvgs() throws IOException, XMLStreamException {
		Preconditions.checkNotNull(inputDir, "Please define the input directory.");
		Preconditions.checkNotNull(output, "Please define the output file.");
		Preconditions.checkNotNull(viewBox, "Please define the SVG viewBox.");

		getLogger().info("Starting svg4j...");
		getLogger().info("Stacking into: {}", viewBox);
		getLogger().info("Searching input directory: {}", inputDir);
		getLogger().info("Stacking to file: {}", output);

		Iterable<Path> input = PathUtils.filterPathsIn(inputDir.toPath(), PathUtils::hasSvgExtension);

		Svg4j svg4j = Svg4j.createStacker()
			.viewBox(viewBox)
			.createSvgStart()
			.hideEmbeddedSvgs()
			.read(input)
			.svgEnd()
			.write(output);

		getLogger().info("Stacked {} XML events.", svg4j.size());
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
