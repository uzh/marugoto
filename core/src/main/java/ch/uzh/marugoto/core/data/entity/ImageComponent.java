package ch.uzh.marugoto.core.data.entity;

public class ImageComponent extends Component {
    private ImageResource image;

    public ImageComponent() {
        super();
    }

    public ImageResource getImage() {
        return image;
    }

    public void setImage(ImageResource image) {
        this.image = image;
    }
}
