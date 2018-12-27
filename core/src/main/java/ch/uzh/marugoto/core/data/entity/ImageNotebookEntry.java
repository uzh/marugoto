package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class ImageNotebookEntry extends NotebookEntry{

	private String caption;
	@Ref
	private ImageResource imageResource;

	public ImageResource getImageResource() {
		return imageResource;
	}

	public void setImageResource(ImageResource imageResource) {
		this.imageResource = imageResource;
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public ImageNotebookEntry() {
		super();
	}
	
	public ImageNotebookEntry(ImageResource imageResource, String caption) {
		super();
		this.imageResource = imageResource;
		this.caption = caption;
	}
}
