package ch.uzh.marugoto.shell.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.VideoComponent;
import ch.uzh.marugoto.core.data.entity.VideoResource;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.core.service.ResourceFactory;
import ch.uzh.marugoto.core.service.ResourceService;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.util.BeanUtil;

public class VideoComponentDeserializer extends StdDeserializer<VideoComponent> {

    public VideoComponentDeserializer() {
        this(null);
    }

    public VideoComponentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public VideoComponent deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        var id = node.get("id");
        var video = node.get("video");
        var page = node.get("page");

        VideoComponent videoComponent = new VideoComponent();

        if (!id.isNull()) {
            videoComponent = (VideoComponent) BeanUtil.getBean(ComponentRepository.class).findById(id.asText()).orElse(videoComponent);
        }

        if (video.isTextual()) {
            try {
                var resourceService = BeanUtil.getBean(ResourceService.class);
                var videoResource = (VideoResource) ResourceFactory.getResource(video.asText());

                videoResource.setPath(resourceService.copyFileToResourceFolder(Paths.get(video.asText())));
                resourceService.saveResource(videoResource);
                videoComponent.setVideo(videoResource);
            } catch (ResourceTypeResolveException e) {
                e.printStackTrace();
            }
        } else if (video.isObject()) {
            videoComponent.setVideo(FileHelper.getMapper().convertValue(video, VideoResource.class));
        }

        if (page.isObject()) {
            videoComponent.setPage(FileHelper.getMapper().convertValue(page, Page.class));
        }

        videoComponent.setNumberOfColumns(node.get("numberOfColumns").asInt());
        videoComponent.setRenderOrder(node.get("renderOrder").asInt());

        return videoComponent;
    }
}
