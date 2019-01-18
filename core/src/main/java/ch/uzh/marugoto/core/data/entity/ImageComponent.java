package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class ImageComponent extends Component {
    @Ref
    private ImageResource image;
    private ImageViewRectangle imageViewRectangle;
    private boolean zoomable;

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

	public ImageComponent() {
		super();
	}
	
	public ImageComponent(ImageResource image) {
		super();
		this.image = image;
	}
	
	public ImageComponent(ImageResource image, ImageViewRectangle imageViewRectangle, boolean zoomable) {
		super();
		this.image = image;
		this.imageViewRectangle = imageViewRectangle;
		this.zoomable = zoomable;
	}
}
