package com.mikebull94.svg4j.util;

import com.google.common.collect.ImmutableList;
import com.mikebull94.svg4j.xml.svg.SvgDocument;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

import static com.google.common.io.Files.getFileExtension;

/**
 * Contains {@link Path} related utility methods.
 */
public final class PathUtils {

	/**
	 * An empty {@link Predicate} that will always return {@code true}.
	 */
	private static final Predicate<Path> UNFILTERED = path -> true;

	/**
	 * A flag indicating whether a {@link Path} is suffixed with the {@link SvgDocument#FILE_EXTENSION}.
	 * @param path The {@link Path}.
	 * @return {@code true} if so, {@code false} otherwise.
	 */
	public static boolean hasSvgExtension(Path path) {
		Path fileName = path.getFileName();

		if (fileName == null) {
			throw new IllegalArgumentException("Path " + path + " has zero elements.");
		}

		return hasSvgExtension(fileName.toString());
	}

	/**
	 * A flag indicating whether a given file name is suffixed with the {@link SvgDocument#FILE_EXTENSION}.
	 * @param fileName The given file name.
	 * @return {@code true} if so, {@code false} otherwise.
	 */
	public static boolean hasSvgExtension(String fileName) {
		return getFileExtension(fileName).equalsIgnoreCase(SvgDocument.FILE_EXTENSION);
	}

	/**
	 * Recursively lists all {@link Path}s.
	 * @param start The {@link Path} to start at.
	 * @return An {@link ImmutableList} of {@link Path}s in the directory and all sub-directories.
	 * @throws IOException If an I/O error occurs.
	 */
	public static ImmutableList<Path> findPathsIn(Path start) throws IOException {
		return filterPathsIn(start, UNFILTERED);
	}

	/**
	 * Recursively lists all {@link Path}s that satisfy a {@link Predicate}.
	 * @param start The {@link Path} to start at.
	 * @param filter The {@link Predicate} to filter {@link Path}s with.
	 * @return An {@link ImmutableList} of filtered {@link Path}s in the directory and all sub-directories.
	 * @throws IOException If an I/O error occurs.
	 */
	public static ImmutableList<Path> filterPathsIn(Path start, Predicate<Path> filter) throws IOException {
		ImmutableList.Builder<Path> filtered = ImmutableList.builder();

		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				if (filter.test(file)) {
					filtered.add(file);
				}
				return FileVisitResult.CONTINUE;
			}
		});

		return filtered.build();
	}

	private PathUtils() {
		/* empty */
	}
}
