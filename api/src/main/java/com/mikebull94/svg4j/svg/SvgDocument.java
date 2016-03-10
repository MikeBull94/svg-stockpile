package com.mikebull94.svg4j.svg;

import com.google.common.collect.ImmutableSet;
import com.mikebull94.svg4j.xml.XmlDocument;

import javax.xml.namespace.QName;

/**
 * Contains constants related to the <a href="https://www.w3.org/TR/SVG/">SVG</a> document specification.
 * @see <a href="https://www.w3.org/Graphics/SVG/">Scalable Vector Graphics</a>
 * @see <a href="https://www.w3.org/2000/svg">SVG namespace</a>
 */
public final class SvgDocument {

	/**
	 * The file extension used to identify SVG documents.
	 */
	public static final String FILE_EXTENSION = "svg";

	/**
	 * The namespace for SVG documents.
	 */
	public static final String NAMESPACE = "svg";

	/**
	 * The namespace for embedded SVG documents.
	 */
	public static final String EMBEDDED_NAMESPACE = XmlDocument.NAMESPACE + ":" + NAMESPACE;

	/**
	 * The URI to the namespace for SVG documents.
	 */
	public static final String NAMESPACE_URI = "http://www.w3.org/2000/svg";

	/**
	 * The {@code <svg>} tag.
	 */
	public static final QName SVG_TAG = new QName(NAMESPACE_URI, "svg");

	/**
	 * The {@code <g>} (group) tag.
	 */
	public static final QName GROUP_TAG = new QName(NAMESPACE_URI, "g");

	/**
	 * The {@code <metadata>} tag.
	 */
	public static final QName METADATA_TAG = new QName(NAMESPACE_URI, "metadata");

	/**
	 * The {@code <defs>} (definitions) tag.
	 */
	public static final QName DEFS_TAG = new QName(NAMESPACE_URI, "defs");

	/**
	 * The {@link <svg:svg>} tag.
	 */
	public static final QName EMBEDDED_SVG_TAG = new QName(NAMESPACE_URI, "svg", "svg");

	/**
	 * The {@code <svg:style>} tag.
	 */
	public static final QName STYLE_TAG = new QName(NAMESPACE_URI, "style", "svg");

	/**
	 * An {@link ImmutableSet} of {@link QName}s to deem invalid with regards to an optimized SVG specification.
	 */
	private static final ImmutableSet<QName> UNOPTIMIZED = ImmutableSet.of(
		METADATA_TAG,
		DEFS_TAG,
		GROUP_TAG
	);

	/**
	 * A flag indicating whether a {@link QName} is valid for the optimized SVG specification.
	 * @param name The {@link QName}.
	 * @return {@code true} if so, {@code false} otherwise.
	 */
	public static boolean optimized(QName name) {
		return name.getNamespaceURI().equals(NAMESPACE_URI) && !UNOPTIMIZED.contains(name);
	}

	private SvgDocument() {
		/* empty */
	}
}
