package ch.uzh.marugoto.shell.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PdfComponent;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.core.service.ResourceFactory;
import ch.uzh.marugoto.core.service.ResourceService;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.util.BeanUtil;

public class PdfComponentDeserializer extends StdDeserializer<PdfComponent> {

    public PdfComponentDeserializer() {
        this(null);
    }

    public PdfComponentDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public PdfComponent deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        var id = node.get("id");
        var pdf = node.get("pdf");
        var page = node.get("page");
        PdfComponent pdfComponent = new PdfComponent();

        if (!id.isNull()) {
            pdfComponent = (PdfComponent) BeanUtil.getBean(ComponentRepository.class).findById(id.asText()).orElse(pdfComponent);
        }

        if (pdf.isTextual()) {
            try {
                var resourceService = BeanUtil.getBean(ResourceService.class);
                var pdfResource = (PdfResource) ResourceFactory.getResource(pdf.asText());

                pdfResource.setPath(resourceService.copyFileToResourceFolder(Paths.get(pdf.asText())));
                resourceService.saveResource(pdfResource);
                pdfComponent.setPdf(pdfResource);
            } catch (ResourceTypeResolveException e) {
                e.printStackTrace();
            }
        } else if (pdf.isObject()) {
            pdfComponent.setPdf(FileHelper.getMapper().convertValue(pdf, PdfResource.class));
        }

        if (page.isObject()) {
            pdfComponent.setPage(FileHelper.getMapper().convertValue(page, Page.class));
        }

        pdfComponent.setNumberOfColumns(node.get("numberOfColumns").asInt());
        pdfComponent.setRenderOrder(node.get("renderOrder").asInt());

        return pdfComponent;
    }
}
