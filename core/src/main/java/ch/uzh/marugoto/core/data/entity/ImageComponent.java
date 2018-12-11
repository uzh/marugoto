package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.HashIndexed;

public class ImageComponent extends Component {
    @HashIndexed(unique = true)
    private String imageUrl;

    public ImageComponent() {
        super();
    }

    public ImageComponent(int numberOfColumns) {
        super(numberOfColumns);
    }

    public ImageComponent(String imageUrl) {
        this();
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
