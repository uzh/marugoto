package ch.uzh.marugoto.core.service;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.ImageNotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.PdfNotebookEntry;

@Service
public class GeneratePdfService {

	private final static Font titleFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
	private final static Font textFont = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.LIGHT_GRAY);
	private final static Font linkFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, BaseColor.BLUE);
	
	@Value("${marugoto.resource.dir}")
	protected String resourceDirectory;
	
	public ByteArrayInputStream createPdf(List<NotebookEntry> notebookEntries) throws IOException {

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			PdfWriter.getInstance(document, out);
			document.open();
			for (NotebookEntry notebookEntry : notebookEntries) {

				document.add(getTitleStyle(notebookEntry.getTitle()));
				document.add(getTextStyle(notebookEntry.getText()));
				document.add(Chunk.NEWLINE);

				if (notebookEntry instanceof ImageNotebookEntry) {
					String filePath = ((ImageNotebookEntry) notebookEntry).getImage().getPath();
					Path path = Paths.get(resourceDirectory + File.separator + filePath);	
					document.add(getImageStyle(path.toFile().getAbsolutePath()));
				} 
				
				if (notebookEntry instanceof PdfNotebookEntry) {
					String filePath =  ((PdfNotebookEntry) notebookEntry).getPdf().getPath();
					Path path = Paths.get(resourceDirectory + File.separator + filePath);	
					document.add(getPDfStyle(path.toFile().getAbsolutePath()));
				}
				if (notebookEntry instanceof NotebookEntry) {
					document.add(Chunk.NEXTPAGE);
				}
			}
			document.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(out.toByteArray());
	}

	private Paragraph getTitleStyle(String text) {
		Paragraph p = new Paragraph(text, titleFont);
		p.setAlignment(Element.ALIGN_CENTER);
		
		return p;
	}

	private Paragraph getTextStyle(String text) {
		Paragraph p = new Paragraph(text, textFont);
		p.setAlignment(Element.ALIGN_JUSTIFIED);
		
		return p;
	}
	
	private Image getImageStyle (String imagePath) throws BadElementException, MalformedURLException, IOException {
		Image image = Image.getInstance(imagePath);
		image.setAbsolutePosition(100, 100);
        image.scalePercent(50);
		
        return image;
	}
	
	private Paragraph getPDfStyle(String filePath) {
		Paragraph p = new Paragraph("You can find the pdf at the following ", textFont);
	    Anchor anchor = new Anchor("link",linkFont);
	    anchor.setReference(filePath);
	    p.add(anchor);

	    return p;
	}	
}
