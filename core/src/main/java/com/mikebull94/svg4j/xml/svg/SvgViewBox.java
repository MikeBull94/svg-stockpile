package com.mikebull94.svg4j.xml.svg;

import com.google.common.base.MoreObjects;

import java.util.Objects;

/**
 * Represents the view-box of an {@link SvgDocument}.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/viewBox">viewBox</a>
 */
public final class SvgViewBox {

	/**
	 * The format of the viewBox attribute.
	 */
	private static final String VIEW_BOX_FORMAT = "%d %d %d %d";

	/**
	 * The minimum x-coordinate.
	 */
	private final int minX;

	/**
	 * The minimum y-coordinate.
	 */
	private final int minY;

	/**
	 * The width.
	 */
	private final int width;

	/**
	 * The height.
	 */
	private final int height;

	/**
	 * Creates a new {@link SvgViewBox}.
	 * @param minX The minimum x-coordinate.
	 * @param minY The minimum y-coordinate.
	 * @param width The width.
	 * @param height The height.
	 */
	public SvgViewBox(int minX, int minY, int width, int height) {
		this.minX = minX;
		this.minY = minY;
		this.width = width;
		this.height = height;
	}

	/**
	 * Formats the {@link SvgViewBox#minX},  {@link SvgViewBox#minY},  {@link SvgViewBox#width}, and
	 * {@link SvgViewBox#height} according to the {@link SvgViewBox#VIEW_BOX_FORMAT}.
	 * @return The formatted {@link String}.
	 */
	public String format() {
		return String.format(VIEW_BOX_FORMAT, minX, minY, width, height);
	}

	/**
	 * Gets the minimum x-coordinate.
	 * @return The minimum x-coordinate.
	 */
	public int getMinX() {
		return minX;
	}

	/**
	 * Gets the minimum y-coordinate.
	 * @return The minimum y-coordinate.
	 */
	public int getMinY() {
		return minY;
	}

	/**
	 * Gets the width.
	 * @return The width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height.
	 * @return The height.
	 */
	public int getHeight() {
		return height;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SvgViewBox that = (SvgViewBox) o;
		return minX == that.minX
			&& minY == that.minY
			&& width == that.width
			&& height == that.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(minX, minY, width, height);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("minX", minX)
			.add("minY", minY)
			.add("width", width)
			.add("height", height)
			.toString();
	}
}
