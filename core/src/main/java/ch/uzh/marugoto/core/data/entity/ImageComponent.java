package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class ImageComponent extends Component {
    @Ref
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
