package ch.uzh.marugoto.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.data.entity.topic.ImageNotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.PdfNotebookEntry;
import ch.uzh.marugoto.core.exception.CreatePdfException;


@Service
public class GeneratePdfService {

	private static final int titleFontSize = 16;
	private static final BaseColor titleFontColor = BaseColor.BLACK;
	private static final int textFontSize = 12;
	private static final BaseColor textFontColor = BaseColor.GRAY;
	private static final BaseColor linkFontColor = BaseColor.BLUE;
	private static final BaseColor personalNoteFontColor = BaseColor.DARK_GRAY;
	private static final int dateFontSize = 10;

	@Value("${marugoto.resource.static.dir}")
	protected String resourceStaticDirectory;
	
	public ByteArrayInputStream createPdf(java.util.List<NotebookEntry> notebookEntries) throws CreatePdfException {
		try {
			Rectangle pageSize = new Rectangle(PageSize.A4);
			// set document background color
			pageSize.setBackgroundColor(new BaseColor(242, 240, 238));
			Document document = new Document(pageSize);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			PdfWriter.getInstance(document, out);
			document.open();
			for (NotebookEntry notebookEntry : notebookEntries) {

				document.add(getTitleStyle(notebookEntry.getTitle()));
				document.add(Chunk.NEWLINE);
				document.add(getTextStyle(notebookEntry.getText()));
				LineSeparator ls = new LineSeparator();
				ls.setLineColor(BaseColor.LIGHT_GRAY);
				document.add(new Chunk(ls));
				document.add(getPersonalNoteStyle(notebookEntry));
				document.add(Chunk.NEWLINE);

				if (notebookEntry instanceof ImageNotebookEntry) {
					String filePath = ((ImageNotebookEntry) notebookEntry).getImage().getPath();
					Path path = Paths.get(resourceStaticDirectory + File.separator + filePath);
					document.add(getImageStyle(path.toFile().getAbsolutePath()));
				}

				if (notebookEntry instanceof PdfNotebookEntry) {
					String filePath =  ((PdfNotebookEntry) notebookEntry).getPdf().getPath();
					Path path = Paths.get(resourceStaticDirectory + File.separator + filePath);
					document.add(getPDfStyle(path.toFile().getAbsolutePath()));
				}
				if (notebookEntry instanceof NotebookEntry) {
					document.add(Chunk.NEXTPAGE);
				}
			}
			document.close();

			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException | DocumentException e) {
			throw new CreatePdfException(e.getMessage());
		}

	}

	private Paragraph getTitleStyle(String title) {
		Paragraph p = new Paragraph(title,getLibreFont(titleFontSize, titleFontColor));
		p.setAlignment(Element.ALIGN_CENTER);
				
		return p;
	}

	private Paragraph getTextStyle(String text) {
		Paragraph p = new Paragraph(text,getLibreFont(textFontSize,textFontColor));
		p.setAlignment(Element.ALIGN_JUSTIFIED);
		
		return p;
	}
	
	private List getPersonalNoteStyle(NotebookEntry notebookEntry) {
		List list = new List(List.UNORDERED, 0);
		ListItem notes = new ListItem();
		notes.setFont(getLibreFont(textFontSize, personalNoteFontColor));
		for(PersonalNote note : notebookEntry.getPersonalNotes()) {
			Chunk chunk = new Chunk(formatDate(note.getCreatedAt()),getLibreFont(dateFontSize, personalNoteFontColor));
			notes.add(chunk);
			notes.add(Chunk.NEWLINE);
			notes.add(Chunk.NEWLINE);
			notes.add(note.getMarkdownContent());
			notes.setAlignment(Element.ALIGN_JUSTIFIED);
			notes.add(Chunk.NEWLINE);
		}
		
		list.setIndentationLeft(0); 
		list.setListSymbol(""); 
		list.add(notes);
		return list; 
	}
	
	private Image getImageStyle (String imagePath) throws BadElementException, MalformedURLException, IOException {
		Image image = Image.getInstance(imagePath);
        image.scaleToFit(500, 300);
        image.setRotationDegrees(3);
//	    image.scalePercent(40f);
        image.setAlignment(Element.ALIGN_CENTER);
		
        return image;
	}
	
	private Paragraph getPDfStyle(String filePath) {
		Paragraph p = new Paragraph("You can find the pdf at the following ", getLibreFont(textFontSize, textFontColor));
	    Anchor anchor = new Anchor("link",getLibreFont(textFontSize, linkFontColor));
	    anchor.setReference(filePath);
	    p.add(anchor);

	    return p;
	}
	
	private Font getLibreFont(int size, BaseColor color) {
		FontFactory.register("https://fonts.googleapis.com/css?family=Libre+Baskerville", "Libre");
		Font f = FontFactory.getFont("Libre");
		f.setColor(color);
		f.setSize(size);
		return f;
	}
	
	private String formatDate(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_WITH_TIME);
		return date.format(formatter);
	}
}
