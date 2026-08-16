package dev.morebookmarks.ui;

public record IntRect(int x, int y, int width, int height) {
	public boolean empty() {
		return width <= 0 || height <= 0;
	}
}
