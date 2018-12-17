package ch.uzh.marugoto.core.data.entity;

public class AudioComponent extends Component {

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
