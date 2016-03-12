package com.mikebull94.stockpile.gradle;

import com.google.common.collect.ImmutableList;
import com.mikebull94.stockpile.util.PathUtils;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.io.Files.copy;
import static com.mikebull94.stockpile.gradle.GradleBuildFileBehaviour.arguments;
import static com.mikebull94.stockpile.gradle.GradleBuildFileBehaviour.correctOutput;
import static com.mikebull94.stockpile.gradle.GradleBuildFileBehaviour.gradleFile;
import static com.mikebull94.stockpile.gradle.GradleBuildFileBehaviour.outputContains;
import static com.mikebull94.stockpile.gradle.GradleBuildFileBehaviour.taskOutcome;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

public final class StockpilePluginTest {
	@Rule
	public final TemporaryFolder projectDir = new TemporaryFolder();

	private File buildFile;

	@Before
	public void setUp() throws IOException {
		buildFile = projectDir.newFile("build.gradle");

		Path inputDir = Paths.get("src/integTest/resources/svgs");
		ImmutableList<Path> input = PathUtils.filterPathsIn(inputDir, PathUtils::hasSvgExtension);

		for (Path path : input) {
			Path fileName = path.getFileName();

			if (fileName != null) {
				File file = projectDir.newFile(fileName.toString());
				copy(path.toFile(), file);
			}
		}
	}

	@Test
	public void customTask() {
		GradleBuildFileTester.test(projectDir, buildFile)
			.given(gradleFile("customTask"))
			.given(arguments("stockpileSvgs"))
			.given(GradleRunner::forwardOutput)
			.when(GradleRunner::build)
			.thenBuildResult("Expected start debug output.", outputContains("Starting SVG stockpile..."))
			.thenBuildResult("Expected outcome of :stackSvgs to be " + SUCCESS, taskOutcome(":stockpileSvgs", SUCCESS))
			.thenBuildResult("Expected finish debug output", outputContains("Finished stockpiling SVGs."))
			.thenTemporaryFolder("Actual SVG does not match expected", correctOutput("output.svg"));
	}
}
