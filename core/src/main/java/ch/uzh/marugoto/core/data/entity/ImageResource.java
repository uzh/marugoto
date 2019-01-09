package ch.uzh.marugoto.core.data.entity;

public class ImageResource extends Resource {
    private String thumbnailPath;

    public ImageResource() {
        super();
    }

    public ImageResource(String path) {
        super(path);
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
}
