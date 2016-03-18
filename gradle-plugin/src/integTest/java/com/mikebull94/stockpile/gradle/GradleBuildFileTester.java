package com.mikebull94.stockpile.gradle;

import org.gradle.testkit.jarjar.com.google.common.base.Preconditions;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;

final class GradleBuildFileTester {
	static GradleBuildFileTester test(TemporaryFolder projectDir, File buildFile) {
		return new GradleBuildFileTester(projectDir, buildFile);
	}

	private final TemporaryFolder projectDir;
	private final File buildFile;
	private GradleRunner runner;
	private BuildResult result;

	private GradleBuildFileTester(TemporaryFolder projectDir, File buildFile) {
		this.projectDir = Preconditions.checkNotNull(projectDir);
		this.buildFile = Preconditions.checkNotNull(buildFile);
		this.runner = GradleRunner.create().withProjectDir(projectDir.getRoot());
	}

	GradleBuildFileTester given(BiConsumer<TemporaryFolder, File> consumer) {
		consumer.accept(projectDir, buildFile);
		return this;
	}

	GradleBuildFileTester given(Function<GradleRunner, GradleRunner> function) {
		runner = function.apply(runner);
		return this;
	}

	GradleBuildFileTester when(Function<GradleRunner, BuildResult> function) {
		result = function.apply(runner);
		return this;
	}

	GradleBuildFileTester thenBuildResult(String message, Predicate<BuildResult> predicate) {
		assertTrue(message, predicate.test(result));
		return this;
	}

	GradleBuildFileTester thenTemporaryFolder(String message, Predicate<TemporaryFolder> predicate) {
		assertTrue(message, predicate.test(projectDir));
		return this;
	}
}
