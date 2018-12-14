package ch.uzh.marugoto.core.data.entity;

import com.arangodb.springframework.annotation.Document;

import org.springframework.data.annotation.Id;

@Document("resource")
abstract public class Resource {
    @Id
    private String id;
    private String path;

    public Resource() {
        super();
    }

    public Resource(String path) {
        this();
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
