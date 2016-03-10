package com.mikebull94.svg4j.gradle;

import com.mikebull94.svg4j.util.PathUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

final class GradleBuildFileBehaviour {
	private static final String BUILD_FILE_PATH_FORMAT = "build/resources/integTest/%s.gradle";
	private static final Path EXPECTED = Paths.get("build/resources/integTest/expected.svg");

	public static BiConsumer<TemporaryFolder, File> gradleFile(String name) {
		return (folder, buildFile) -> {
			Path path = Paths.get(String.format(BUILD_FILE_PATH_FORMAT, name));

			try (BufferedWriter writer = Files.newBufferedWriter(buildFile.toPath())) {
				List<String> input = Files.readAllLines(path);

				for (String line : input) {
					writer.write(line);
					writer.newLine();
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}

	public static Function<GradleRunner, GradleRunner> arguments(String... arguments) {
		return input -> input.withArguments(arguments);
	}

	public static Predicate<BuildResult> outputContains(CharSequence text) {
		return result -> result.getOutput().contains(text);
	}

	public static Predicate<BuildResult> taskOutcome(String taskName, TaskOutcome outcome) {
		return result -> result.task(taskName).getOutcome() == outcome;
	}

	public static Predicate<TemporaryFolder> correctOutput(String outputFile) {
		return projectDir -> {
			try {
				Path output = projectDir.getRoot().toPath().resolve(outputFile);
				String expected = PathUtils.contents(output);
				String actual = PathUtils.contents(EXPECTED);
				return expected.equals(actual);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}

	private GradleBuildFileBehaviour() {
		/* empty */
	}
}
