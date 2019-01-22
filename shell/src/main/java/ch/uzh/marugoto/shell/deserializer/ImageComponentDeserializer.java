package ch.uzh.marugoto.shell.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.data.entity.ImageComponent;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;
import ch.uzh.marugoto.core.service.ImageService;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.util.BeanUtil;

@SuppressWarnings("serial")
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
        var page = node.get("page");
        var numberOfColumns = node.get("numberOfColumns").asInt();
//        var imageViewRectangle = node.get("imageViewRectangle");

        var imageService = BeanUtil.getBean(ImageService.class);
        ImageComponent imageComponent = new ImageComponent();

        if (!id.isNull()) {
            imageComponent = (ImageComponent) BeanUtil.getBean(ComponentRepository.class).findById(id.asText()).orElse(imageComponent);
        }

        if (image.isTextual()) {
            try {
                var imagePath = Paths.get(image.asText());
                // save resized image
                var imageResource = imageService.saveImageResource(imagePath, numberOfColumns);
                imageComponent.setImage(imageResource);
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            }
        } else if (image.isObject()) {
            imageComponent.setImage(FileHelper.getMapper().convertValue(image, ImageResource.class));
        }

        if (page.isObject()) {
            imageComponent.setPage(FileHelper.getMapper().convertValue(page, Page.class));
        }

        imageComponent.setNumberOfColumns(numberOfColumns);
        imageComponent.setRenderOrder(node.get("renderOrder").asInt());
      //  imageComponent.setZoomable(node.get("zoomable").asBoolean());
      //  imageComponent.setImageViewRectangle(FileHelper.getMapper().convertValue(imageViewRectangle, ImageViewRectangle.class));
        return imageComponent;
    }
}
