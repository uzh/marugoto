package ch.uzh.marugoto.shell.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.data.entity.AudioComponent;
import ch.uzh.marugoto.core.data.entity.AudioResource;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.core.service.ResourceFactory;
import ch.uzh.marugoto.core.service.ResourceService;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.util.BeanUtil;

public class AudioComponentDeserializer extends StdDeserializer<AudioComponent> {

    public AudioComponentDeserializer() {
        this(null);
    }

    public AudioComponentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AudioComponent deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        var id = node.get("id");
        var audio = node.get("audio");
        var page = node.get("page");

        AudioComponent audioComponent = new AudioComponent();

        if (!id.isNull()) {
            audioComponent = (AudioComponent) BeanUtil.getBean(ComponentRepository.class).findById(id.asText()).orElse(audioComponent);
        }

        if (audio.isTextual()) {
            try {
                var resourceService = BeanUtil.getBean(ResourceService.class);
                var audioResource = (AudioResource) ResourceFactory.getResource(audio.asText());
                var audioPath = Paths.get(audio.asText());

                audioResource.setPath(resourceService.copyFileToResourceFolder(audioPath));
                resourceService.saveResource(audioResource);
                audioComponent.setAudio(audioResource);
                // save resized image
            } catch (ResourceTypeResolveException e) {
                e.printStackTrace();
            }
        } else if (audio.isObject()) {
            audioComponent.setAudio(FileHelper.getMapper().convertValue(audio, AudioResource.class));
        }

        if (page.isObject()) {
            audioComponent.setPage(FileHelper.getMapper().convertValue(page, Page.class));
        }

        audioComponent.setNumberOfColumns(node.get("numberOfColumns").asInt());
        audioComponent.setRenderOrder(node.get("renderOrder").asInt());
        return audioComponent;
    }
}
