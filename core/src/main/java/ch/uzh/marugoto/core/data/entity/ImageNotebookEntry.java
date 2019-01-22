package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class ImageNotebookEntry extends NotebookEntry {

	private String caption;
	@Ref
	private ImageResource image;

	public ImageResource getImage() {
		return image;
	}

	public void setImage(ImageResource image) {
		this.image = image;
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
	
	public ImageNotebookEntry(ImageResource image, String caption,Page page, String title,String text) {
		super(page,title,text);
		this.image = image;
		this.caption = caption;
	}
}
