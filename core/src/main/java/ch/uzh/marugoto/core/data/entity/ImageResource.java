package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.HashIndexed;

public class ImageResource implements Resource {
    @HashIndexed(unique = true)
    private String path;

    public ImageResource(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
