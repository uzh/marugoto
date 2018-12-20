package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Ref;

public class AudioComponent extends Component {
    @Ref
    private AudioResource audio;

    public AudioComponent() {
        super();
    }

    public AudioComponent(AudioResource audio) {
        this();
        this.audio = audio;
    }

    public AudioResource getAudio() {
        return audio;
    }

    public void setAudio(AudioResource audio) {
        this.audio = audio;
    }
}
