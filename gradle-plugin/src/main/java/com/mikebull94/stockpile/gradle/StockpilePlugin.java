package com.mikebull94.stockpile.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public final class StockpilePlugin implements Plugin<Project> {
	public static final String GROUP = "svg-stockpile";

	@Override
	public void apply(Project project) {
		project.getLogger().info("Applied svg-stockpile Gradle plugin.");
	}
}
