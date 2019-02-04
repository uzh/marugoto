package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class ImageComponent extends Component {
    @Ref
    private ImageResource image;
    private ImageViewRectangle imageViewRectangle;
    private boolean zoomable;
    private String caption;

    public ImageResource getImage() {
        return image;
    }

    public void setImage(ImageResource image) {
        this.image = image;
    }

	public ImageViewRectangle getImageViewRectangle() {
		return imageViewRectangle;
	}

	public boolean isZoomable() {
		return zoomable;
	}

	public void setImageViewRectangle(ImageViewRectangle imageViewRectangle) {
		this.imageViewRectangle = imageViewRectangle;
	}

	public void setZoomable(boolean zoomable) {
		this.zoomable = zoomable;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public ImageComponent() {
		super();
	}
	
	public ImageComponent (ImageResource image, String caption) {
		this.image = image;
		this.caption = caption;
	}
	
	public ImageComponent(ImageResource image, ImageViewRectangle imageViewRectangle, boolean zoomable) {
		super();
		this.image = image;
		this.imageViewRectangle = imageViewRectangle;
		this.zoomable = zoomable;
	}
}
