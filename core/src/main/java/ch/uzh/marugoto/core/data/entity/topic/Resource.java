package ch.uzh.marugoto.core.data.entity.topic;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Document;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImageResource.class, name = "image"),
        @JsonSubTypes.Type(value = AudioResource.class, name = "audio"),
        @JsonSubTypes.Type(value = VideoResource.class, name = "video"),
        @JsonSubTypes.Type(value = PdfResource.class, name = "pdf")
})
@Document("resource")
public abstract class Resource {
    @Id
    private String id;
    private String path;

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
