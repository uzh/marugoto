package ch.uzh.marugoto.core.data.entity;

public class VideoComponent extends Component {

    private VideoResource video;

    public VideoComponent() {
        super();
    }

    public VideoComponent(VideoResource video) {
        this();
        this.video = video;
    }

    public VideoResource getVideo() {
        return video;
    }

    public void setVideo(VideoResource video) {
        this.video = video;
    }
}
