package com.mikebull94.stockpile.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * A {@link Plugin} that interacts with the svg-stockpile API.
 */
public final class StockpilePlugin implements Plugin<Project> {

	/**
	 * Applies this {@link Plugin} to a {@link Project}.
	 * @param project The {@link Project} to apply this {@link Plugin} to.
	 */
	@Override
	public void apply(Project project) {
		Task task = project.getTasks().create(StockpileTask.NAME, StockpileTask.class);
		task.setGroup(StockpileTask.GROUP);
		task.setDescription(StockpileTask.DESCRIPTION);
	}
}
