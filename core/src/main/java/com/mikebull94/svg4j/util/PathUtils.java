package com.mikebull94.svg4j.util;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Contains {@link Path} related utility methods.
 */
public final class PathUtils {

	/**
	 * Recursively lists all {@link Path}s in a directory.
	 * @param path The {@link Path} of the directory to scan.
	 * @return An {@link ImmutableList} of {@link Path}s in the directory and all sub-directories.
	 * @throws IOException If an I/O error occurs.
	 */
	public static ImmutableList<Path> listRecursive(Path path) throws IOException {
		return listRecursive(path, p -> true);
	}

	/**
	 * Recursively lists all {@link Path}s in a directory.
	 * @param path The {@link Path} of the directory to scan.
	 * @param filter The {@link Predicate} to apply to each {@link Path}.
	 * @return An {@link ImmutableList} of filtered {@link Path}s in the directory and all sub-directories.
	 * @throws IOException If an I/O error occurs.
	 */
	public static ImmutableList<Path> listRecursive(Path path, Predicate<Path> filter) throws IOException {
		ImmutableList.Builder<Path> builder = ImmutableList.builder();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					builder.addAll(listRecursive(path, filter));
				} else if (filter.test(entry)) {
					builder.add(entry);
				}
			}
		}

		return builder.build();
	}

	private PathUtils() {
		/* empty */
	}
}
