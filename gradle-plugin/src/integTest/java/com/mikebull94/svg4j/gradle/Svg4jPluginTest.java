package com.mikebull94.svg4j.gradle;

import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.util.PathUtils;
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
import static com.mikebull94.svg4j.gradle.GradleBuildFileBehaviour.arguments;
import static com.mikebull94.svg4j.gradle.GradleBuildFileBehaviour.correctOutput;
import static com.mikebull94.svg4j.gradle.GradleBuildFileBehaviour.gradleFile;
import static com.mikebull94.svg4j.gradle.GradleBuildFileBehaviour.outputContains;
import static com.mikebull94.svg4j.gradle.GradleBuildFileBehaviour.taskOutcome;
import static com.mikebull94.svg4j.gradle.GradleBuildFileTester.test;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

public final class Svg4jPluginTest {
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
		test(projectDir, buildFile)
			.given(gradleFile("customTask"))
			.given(arguments("stackSvgs"))
			.given(GradleRunner::forwardOutput)
			.when(GradleRunner::build)
			.thenBuildResult(outputContains("Starting SVG stacking..."))
			.thenBuildResult(outputContains("Finished stacking SVGs."))
			.thenBuildResult(taskOutcome(":stackSvgs", SUCCESS))
			.thenTemporaryFolder(correctOutput("output.svg"));
	}
}
