package ch.uzh.marugoto.core.data.entity.topic;

import com.arangodb.springframework.annotation.Ref;

import java.util.List;

public class ImageComponent extends Component {
    @Ref
    private List<ImageResource> images;
    private ImageViewRectangle imageViewRectangle;
    private boolean zoomable;
    private String caption;

    public List<ImageResource> getImages() {
        return images;
    }

    public void setImages(List<ImageResource> images) {
        this.images = images;
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
}
