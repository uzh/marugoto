package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;

/**
 * 
 * Base Component entity
 *
 */
abstract public class Component {
	@Id
	private String id;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Component() {
		super();
	}

	public Component(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
