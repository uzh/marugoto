package ch.uzh.marugoto.shell.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ch.uzh.marugoto.core.data.entity.topic.AudioResource;
import ch.uzh.marugoto.core.data.entity.topic.DocumentResource;
import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.Resource;
import ch.uzh.marugoto.core.data.entity.topic.VideoResource;

public class ResourceDeserializer extends StdDeserializer<Resource> {

	private static final long serialVersionUID = 8106372887957310804L;

	public ResourceDeserializer() {
        super(Resource.class);
    }

    @Override
    public Resource deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        JsonNode node = p.getCodec().readTree(p);

        switch (node.get("type").asText()) {
            case "ImageResource":
                return p.getCodec().treeToValue(node, ImageResource.class);
            case "AudioResource":
                return p.getCodec().treeToValue(node, AudioResource.class);
            case "VideoResource":
                return p.getCodec().treeToValue(node, VideoResource.class);
            case "DocumentResource":
                return p.getCodec().treeToValue(node, DocumentResource.class);
            default:
                return null;
        }
    }
}
