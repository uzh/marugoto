package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class VideoComponent extends Component {
    @Ref
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
