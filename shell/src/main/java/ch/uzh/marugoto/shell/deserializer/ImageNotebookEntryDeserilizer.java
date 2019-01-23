package ch.uzh.marugoto.shell.deserializer;

import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.ImageNotebookEntry;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.MailExercise;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.exception.ResizeImageException;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.core.service.ImageService;
import ch.uzh.marugoto.shell.util.BeanUtil;

@SuppressWarnings("serial")
public class ImageNotebookEntryDeserilizer extends StdDeserializer<ImageNotebookEntry> {

	public ImageNotebookEntryDeserilizer() {
		this(null);
	}
	
	public ImageNotebookEntryDeserilizer(Class<?> vc) {
		super(vc);
	}

	@Override
	public ImageNotebookEntry deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {

		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		var id = node.get("id");
		var image = node.get("image");
		var page = node.get("page");
		var mailExercise = node.get("mailExercise");
		var dialogResponse = node.get("dialogResponse");
		var addedToPageState = node.get("addToPageStateAt");
		
		var imageService = BeanUtil.getBean(ImageService.class);
		ImageNotebookEntry imageNotebookEntry = new ImageNotebookEntry();
		
		if(!id.isNull()) {
			imageNotebookEntry = (ImageNotebookEntry) BeanUtil.getBean(NotebookEntryRepository.class).findById(id.asText()).orElse(imageNotebookEntry);
		}
		
		if(image.isTextual()) {
			
			try {
				var imagePath = Paths.get(image.asText());
				var imageResource = imageService.saveImageResource(imagePath);

				imageNotebookEntry.setImage(imageResource);
			} catch (ResourceNotFoundException | ResizeImageException e) {
				e.printStackTrace();
			}

		} else if (image.isObject()) {
			imageNotebookEntry.setImage(FileHelper.getMapper().convertValue(image, ImageResource.class));
		}
		
		if (page.isObject()) {
			imageNotebookEntry.setPage(FileHelper.getMapper().convertValue(page, Page.class));
		}
		
		if (mailExercise.isObject()) {
			imageNotebookEntry.setMailExercise(FileHelper.getMapper().convertValue(mailExercise, MailExercise.class));
		}
		
		if (dialogResponse.isObject()) {
			imageNotebookEntry.setDialogResponse(FileHelper.getMapper().convertValue(dialogResponse, DialogResponse.class));
		}
		
		if (addedToPageState.isTextual()) {
			imageNotebookEntry.setAddToPageStateAt(NotebookEntryAddToPageStateAt.valueOf(addedToPageState.asText()));
		}
		
		imageNotebookEntry.setCaption(node.get("caption").asText());
		imageNotebookEntry.setText(node.get("text").asText());
		imageNotebookEntry.setTitle(node.get("title").asText());
		
		return imageNotebookEntry;
	}
	

}
