package ch.uzh.marugoto.shell.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.File;
import java.io.IOException;

import ch.uzh.marugoto.core.data.entity.ImageComponent;
import ch.uzh.marugoto.core.service.ImageService;

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

        ImageComponent imageComponent = new ImageComponent(numberOfColumns);

        if (node.has("imageUrl")) {
            var imageUrl = node.get("imageUrl").asText();
            // check if file is readable as image
            var image = ImageService.readImage(imageUrl);

            if (numberOfColumns > 0) {
                var calcWidth = (ImageService.MAX_WIDTH/ImageService.MAX_COLUMNS) * numberOfColumns;
                if (image.getWidth(null) > calcWidth) {
                    imageUrl = ImageService.resizeImage(new File(imageUrl), calcWidth).getAbsolutePath();
                }
            }

            imageComponent.setImageUrl(imageUrl);
        }

        return imageComponent;
    }
}
