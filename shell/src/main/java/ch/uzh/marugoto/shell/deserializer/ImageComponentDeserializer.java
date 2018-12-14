package ch.uzh.marugoto.shell.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.data.entity.ImageComponent;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;
import ch.uzh.marugoto.core.service.ImageService;
import ch.uzh.marugoto.shell.util.BeanUtil;

public class ImageComponentDeserializer extends StdDeserializer<ImageComponent> {

    public ImageComponentDeserializer() {
        this(null);
    }
    public ImageComponentDeserializer(Class<?> vc) {
        super(vc);
    }

    public ImageComponent deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        var numberOfColumns = node.get("numberOfColumns").asInt();
        var id = node.get("id");

        ImageComponent imageComponent = new ImageComponent();

        if (!id.isNull()) {
            imageComponent = (ImageComponent) BeanUtil.getBean(ComponentRepository.class).findById(id.asText()).orElse(null);
        } else {
            if (node.has("image") && node.get("image").isTextual()) {
                try {
                    var imageResource = ImageService.getImage(node.get("image").asText(), numberOfColumns);
                    // save image resource
                    imageResource = BeanUtil.getBean(ResourceRepository.class).save(imageResource);
                    imageComponent.setImage(imageResource);
                } catch (ResourceNotFoundException e) {
                    e.printStackTrace();
                }
            }

            imageComponent.setNumberOfColumns(numberOfColumns);
        }


        return imageComponent;
    }
}
