package dev.morebookmarks.ui;

/**
 * Viewer-specific get/set for the current search string.
 */
public interface SearchAccess {
	String current();

	void apply(String query);
}
