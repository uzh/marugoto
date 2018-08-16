package ch.uzh.marugoto.backend.data.entity;

import com.arangodb.springframework.annotation.Ref;

//import com.arangodb.springframework.annotation.Ref;

abstract public class Component {	
	protected Integer x;
	protected Integer y;
	protected Integer width;
	protected Integer height;

	@Ref
	protected Page page;
	
	public Component(int x, int y, int width, int height, Page page) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.page = page;
	}

	public Integer getX() {
		return x;
	}
	public void setX(Integer x) {
		this.x = x;
	}
	public Integer getY() {
		return y;
	}
	public void setY(Integer y) {
		this.y = y;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
}
