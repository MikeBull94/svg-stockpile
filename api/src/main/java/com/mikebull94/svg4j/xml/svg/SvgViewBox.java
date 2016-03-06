package com.mikebull94.svg4j.xml.svg;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import java.util.Objects;

/**
 * Represents the view-box of an {@link SvgDocument}.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/viewBox">viewBox</a>
 */
public final class SvgViewBox {

	/**
	 * Used to create SVG view-box {@link Attribute}s.
	 */
	private static final XMLEventFactory events = XMLEventFactory.newFactory();

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
	 * Creates an {@link ImmutableList} of {@link Attribute}s from this {@link SvgViewBox}.
	 * @return The {@link ImmutableList} of {@link Attribute}s.
	 */
	public ImmutableList<Attribute> attributes() {
		return ImmutableList.of(
			events.createAttribute("width", String.valueOf(width)),
			events.createAttribute("height", String.valueOf(height)),
			events.createAttribute("viewBox", String.format(VIEW_BOX_FORMAT, minX, minY, width, height))
		);
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
