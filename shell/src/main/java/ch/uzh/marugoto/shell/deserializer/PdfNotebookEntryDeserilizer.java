package ch.uzh.marugoto.shell.deserializer;

import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PdfNotebookEntry;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.service.ResourceFactory;
import ch.uzh.marugoto.core.service.ResourceService;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.util.BeanUtil;

@SuppressWarnings("serial")
public class PdfNotebookEntryDeserilizer extends StdDeserializer<PdfNotebookEntry>{

	public PdfNotebookEntryDeserilizer() {
		this(null);
	}
	
	public PdfNotebookEntryDeserilizer(Class<?> vc) {
		super(vc);
	}

	@Override
	public PdfNotebookEntry deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		var id = node.get("id");
		var pdf = node.get("pdf");
		var page = node.get("page");
		var mailExercise = node.get("mailExercise");
		var dialogResponse = node.get("dialogResponse");
		var addedToPageState = node.get("addToPageStateAt");
		
		PdfNotebookEntry pdfNotebookEntry = new PdfNotebookEntry();
		
		if (!id.isNull()) {
			pdfNotebookEntry = (PdfNotebookEntry) BeanUtil.getBean(NotebookEntryRepository.class).findById(id.asText()).orElse(pdfNotebookEntry);
		}
		
		if (pdf.isTextual()) {
			try {
				var resourceService = BeanUtil.getBean(ResourceService.class);
				var pdfResource = (PdfResource) ResourceFactory.getResource(pdf.asText());
				
				pdfResource.setPath(resourceService.copyFileToResourceFolder(Paths.get(pdf.asText())));
				resourceService.saveResource(pdfResource);
				pdfNotebookEntry.setPdf(pdfResource);
				
			} catch (ResourceTypeResolveException e) {
                e.printStackTrace();
            }
		} else if (pdf.isObject()) {
			pdfNotebookEntry.setPdf(FileHelper.getMapper().convertValue(pdf, PdfResource.class));
		}
		
		if (page.isObject()) {
			pdfNotebookEntry.setPage(FileHelper.getMapper().convertValue(page, Page.class));
		}
		
		if (mailExercise.isObject()) {
			pdfNotebookEntry.setMail(FileHelper.getMapper().convertValue(mailExercise, Mail.class));
		}
		
		if (dialogResponse.isObject()) {
			pdfNotebookEntry.setDialogResponse(FileHelper.getMapper().convertValue(dialogResponse, DialogResponse.class));
		}
		
		if (addedToPageState.isTextual()) {
			pdfNotebookEntry.setAddToPageStateAt(NotebookEntryAddToPageStateAt.valueOf(addedToPageState.asText()));
		}
		
		pdfNotebookEntry.setText(node.get("text").asText());
		pdfNotebookEntry.setTitle(node.get("title").asText());
		
		return pdfNotebookEntry;
	}

}
