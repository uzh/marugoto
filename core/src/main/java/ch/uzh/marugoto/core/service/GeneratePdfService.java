package ch.uzh.marugoto.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

import ch.uzh.marugoto.core.data.entity.ImageNotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.PdfNotebookEntry;

public abstract class GeneratePdfService {

	private final static Font titleFont = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
	private final static Font textFont = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.LIGHT_GRAY);

	public static ByteArrayInputStream createPdf(List<NotebookEntry> notebookEntries)
			throws MalformedURLException, IOException {

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			PdfWriter.getInstance(document, out);
			document.open();
			for (NotebookEntry notebookEntry : notebookEntries) {

				if (notebookEntry instanceof ImageNotebookEntry) {
					
//					Path path = Paths.get(((ImageNotebookEntry) notebookEntry).getImage().getPath());
//			        File imageFile = new File(path.toFile().getAbsolutePath());
				    String imageFile = "http://www.eclipse.org/xtend/images/java8_logo.png";

			        Image image = Image.getInstance(new URL(imageFile));
					document.add(image);
					
				} else if (notebookEntry instanceof PdfNotebookEntry) {
					// ((PdfNotebookEntry) notebookEntry).getPdf().getPath();
				}
				document.add(getTitleStyle(notebookEntry.getTitle()));
				document.add(Chunk.NEWLINE);
				document.add(getTextStyle(notebookEntry.getText()));
				document.add(Chunk.NEXTPAGE);
			}
			document.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(out.toByteArray());
	}

	private static Paragraph getTitleStyle(String text) {
		Paragraph p = new Paragraph(text, titleFont);
		p.setAlignment(Element.ALIGN_CENTER);
		return p;
	}

	private static Paragraph getTextStyle(String text) {
		Paragraph p = new Paragraph(text, textFont);
		p.setAlignment(Element.ALIGN_JUSTIFIED);
		return p;
	}
}
