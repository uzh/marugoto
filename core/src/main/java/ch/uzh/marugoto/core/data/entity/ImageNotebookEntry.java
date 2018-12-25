package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class ImageNotebookEntry extends NotebookEntry{

	@Ref
	private ImageResource imageResource;

	public ImageResource getImageResource() {
		return imageResource;
	}

	public void setImageResource(ImageResource imageResource) {
		this.imageResource = imageResource;
	}
	
	public ImageNotebookEntry() {
		super();
	}
	
	public ImageNotebookEntry(ImageResource imageResource) {
		super();
		this.imageResource = imageResource;
	}
}
