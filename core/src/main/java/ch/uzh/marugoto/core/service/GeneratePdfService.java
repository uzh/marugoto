package ch.uzh.marugoto.core.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.lowagie.text.DocumentException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.exception.CreatePdfException;
import ch.uzh.marugoto.core.helpers.HandlebarHelper;


@Service
public class GeneratePdfService {

    @Value("${marugoto.resource.static.dir}")
    protected String resourceStaticDirectory;
    private GameState gameState;
    private Handlebars handlebars;

    /**
     * Pdf creation logic
     *
     * @param notebookEntries
     * @return
     * @throws CreatePdfException
     */
    public ByteArrayInputStream createPdf(java.util.List<NotebookEntryState> notebookEntries) throws CreatePdfException {
        if (notebookEntries.isEmpty()) {
            throw new CreatePdfException("Notebook has no pages");
        }

        handlebars = new Handlebars();
        handlebars.registerHelpers(new HandlebarHelper());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            // load the template (.hbs file) from classpath or an external file
            Template template = handlebars.compile("pdf-templates/notebook");
            gameState = notebookEntries.get(0).getGameState();

            // run Handlebars render with the input data
            HashMap<String, Object> data = new HashMap<>();
            data.put("gameState", gameState);
            data.put("notebookEntries", notebookEntries);
            data.put("resourceStaticDirectory", resourceStaticDirectory);
            data.put("uploadDirectory", UploadExerciseService.getUploadDirectory());
            String mergedTemplate = template.apply(data);
            // create a structured document from the generated HTML
            Document doc = getDocumentBuilder().parse(new ByteArrayInputStream(mergedTemplate.getBytes("UTF-8")));

            // now we take the document and apply the magic of flying saucer to create a PDF file
            ITextRenderer renderer = new ITextRenderer();

            renderer.setDocument(doc, null);
            renderer.layout();
            renderer.createPDF(out);
            renderer.finishPDF();

        } catch (IOException | ParserConfigurationException | SAXException | DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }



    // we use this method to create a DocumentBuild not so fanatic about XHTML
    private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setNamespaceAware(false);
        fac.setValidating(false);
        fac.setFeature("http://xml.org/sax/features/namespaces", false);
        fac.setFeature("http://xml.org/sax/features/validation", false);
        fac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return fac.newDocumentBuilder();
    }
}