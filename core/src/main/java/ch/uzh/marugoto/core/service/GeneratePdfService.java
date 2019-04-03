package ch.uzh.marugoto.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import ch.uzh.marugoto.core.data.entity.state.NotebookContent;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.ImageComponent;
import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.TextComponent;
import ch.uzh.marugoto.core.exception.CreatePdfException;


@Service
public class GeneratePdfService {

	private static final int titleFontSize = 16;
	private static final BaseColor titleFontColor = BaseColor.BLACK;
	private static final int textFontSize = 12;
	private static final BaseColor textFontColor = BaseColor.GRAY;
//	private static final BaseColor linkFontColor = BaseColor.BLUE;
	private static final BaseColor personalNoteFontColor = BaseColor.DARK_GRAY;
	private static final int dateFontSize = 10;

	@Value("${marugoto.resource.static.dir}")
	protected String resourceStaticDirectory;
	
	public ByteArrayInputStream createPdf(java.util.List<NotebookEntryState> notebookEntries) throws CreatePdfException {
		try {
			if (notebookEntries.isEmpty()) {
				throw new CreatePdfException("Notebook has no pages");
			}

			Rectangle pageSize = new Rectangle(PageSize.A4);
			// set document background color
			pageSize.setBackgroundColor(new BaseColor(242, 240, 238));
			Document document = new Document(pageSize);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			PdfWriter.getInstance(document, out);
			document.open();

			for (NotebookEntryState notebookEntryState : notebookEntries) {
				document.add(getTitleStyle(notebookEntryState.getNotebookEntry().getTitle()));

				for (NotebookContent notebookContent : notebookEntryState.getNotebookContent()) {
					document.add(Chunk.NEWLINE);

					Component component = notebookContent.getComponent();

					if (component instanceof ImageComponent) {
						for (ImageResource imageResource : ((ImageComponent) component).getImages()) {
							Path path = Paths.get(resourceStaticDirectory + File.separator + imageResource.getPath());
							document.add(getImageStyle(path.toFile().getAbsolutePath()));
						}
					}

					if (component instanceof TextComponent) {
						document.add(getTextStyle(((TextComponent) component).getMarkdownContent()));
						LineSeparator ls = new LineSeparator();
						ls.setLineColor(BaseColor.LIGHT_GRAY);
						document.add(new Chunk(ls));
					}

					if (notebookContent.getPersonalNote() != null) {
						PersonalNote personalNote = notebookContent.getPersonalNote();
						Chunk dateChunk = new Chunk(personalNote.getCreatedAt(), getLibreFont(dateFontSize, personalNoteFontColor));
						document.add(dateChunk);
						document.add(getPersonalNoteStyle(personalNote));
						document.add(Chunk.NEWLINE);
					}
				}

				document.add(Chunk.NEXTPAGE);
//				if (notebookEntry instanceof PdfNotebookEntry) {
//					String filePath =  ((PdfNotebookEntry) notebookEntry).getPdf().getPath();
//					Path path = Paths.get(resourceStaticDirectory + File.separator + filePath);
//					document.add(getPDfStyle(path.toFile().getAbsolutePath()));
//				}
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
	
	private Paragraph getPersonalNoteStyle(PersonalNote personalNote) {
		Paragraph p = new Paragraph(personalNote.getMarkdownContent(), getLibreFont(textFontSize, personalNoteFontColor));
		p.setAlignment(Element.ALIGN_JUSTIFIED);
		return p;
	}
	
	private Image getImageStyle (String imagePath) throws BadElementException, IOException {
		Image image = Image.getInstance(imagePath);
        image.scaleToFit(500, 300);
        image.setRotationDegrees(3);
//	    image.scalePercent(40f);
        image.setAlignment(Element.ALIGN_CENTER);
		
        return image;
	}
	
//	private Paragraph getPDfStyle(String filePath) {
//		Paragraph p = new Paragraph("You can find the pdf at the following ", getLibreFont(textFontSize, textFontColor));
//	    Anchor anchor = new Anchor("link",getLibreFont(textFontSize, linkFontColor));
//	    anchor.setReference(filePath);
//	    p.add(anchor);
//
//	    return p;
//	}
	
	private Font getLibreFont(int size, BaseColor color) {
		FontFactory.register("https://fonts.googleapis.com/css?family=Libre+Baskerville", "Libre");
		Font f = FontFactory.getFont("Libre");
		f.setColor(color);
		f.setSize(size);
		return f;
	}
}
