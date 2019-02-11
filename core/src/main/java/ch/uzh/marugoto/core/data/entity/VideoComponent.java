package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class VideoComponent extends Component {
    @Ref
    private VideoResource video;
    private String caption;

    public VideoComponent() {
        super();
    }

    public VideoComponent(VideoResource video) {
        this();
        this.video = video;
    }
    
    public VideoComponent(VideoResource video, String caption) {
        this();
        this.video = video;
        this.caption = caption;
    }

    public VideoResource getVideo() {
        return video;
    }

    public void setVideo(VideoResource video) {
        this.video = video;
    }

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
}
