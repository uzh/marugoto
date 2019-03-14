package ch.uzh.marugoto.core.data.entity.topic;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document("resource")
public abstract class Resource {
    @Id
    private String id;
    private String path;
    @JsonProperty
    private String type = getClass().getSimpleName();

    public Resource() {
        super();
    }

    public Resource(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
