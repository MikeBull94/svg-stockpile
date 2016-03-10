package com.mikebull94.svg4j.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public final class Svg4jPlugin implements Plugin<Project> {
	public static final String GROUP = "svg4j";

	@Override
	public void apply(Project project) {
		project.getLogger().info("Applied svg4j Gradle plugin.");
	}
}
