package ch.uzh.marugoto.shell.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ch.uzh.marugoto.core.data.entity.ImageComponent;
import ch.uzh.marugoto.core.data.entity.ImageResource;
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
        var id = node.get("id");
        var image = node.get("image");
        var numberOfColumns = node.get("numberOfColumns").asInt();
        var resourceRepository = BeanUtil.getBean(ResourceRepository.class);
        ImageComponent imageComponent = new ImageComponent();
        ImageResource imageResource;

        if (!id.isNull()) {
            imageComponent = (ImageComponent) BeanUtil.getBean(ComponentRepository.class).findById(id.asText()).orElse(imageComponent);
        }

        if (image.isTextual()) {
            try {
                var imagePath = node.get("image").asText();
                var newImageWidth = ImageService.getImageWidthForColumns(imagePath, numberOfColumns);
                imageResource = ImageService.resizeImage(imagePath, newImageWidth);
                // save image resource
                imageResource = resourceRepository.save(imageResource);
                imageComponent.setImage(imageResource);
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            }
        } else if (image.isObject()) {
            imageResource = (ImageResource) resourceRepository.findById(image.get("id").asText()).orElse(null);

            if (imageResource != null) {
                imageComponent.setImage(imageResource);
            }
        }

        imageComponent.setNumberOfColumns(numberOfColumns);
        return imageComponent;
    }
}
