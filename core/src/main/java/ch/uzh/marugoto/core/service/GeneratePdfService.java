package ch.uzh.marugoto.core.service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.state.NotebookContent;
import ch.uzh.marugoto.core.data.entity.state.NotebookEntryState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.data.entity.topic.AudioComponent;
import ch.uzh.marugoto.core.data.entity.topic.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.DateExercise;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseOption;
import ch.uzh.marugoto.core.data.entity.topic.ImageComponent;
import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.LinkComponent;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.topic.TextComponent;
import ch.uzh.marugoto.core.data.entity.topic.TextExercise;
import ch.uzh.marugoto.core.data.entity.topic.UploadExercise;
import ch.uzh.marugoto.core.data.entity.topic.VideoComponent;
import ch.uzh.marugoto.core.exception.CreatePdfException;


@Service
public class GeneratePdfService {

	private static final int titleFontSize = 16;
	private static final int textFontSize = 12;
	private static final int captionFontSize = 9;
	private static final BaseColor titleFontColor = BaseColor.DARK_GRAY;
	private static final BaseColor textFontColor = BaseColor.DARK_GRAY;
	private static final BaseColor captionFontColor = BaseColor.GRAY;
	private static final BaseColor personalNoteFontColor = BaseColor.BLACK;

	@Value("${marugoto.resource.static.dir}")
	protected String resourceStaticDirectory;
	@Value("${marugoto.resource.temp.dir}")
	protected String resourceDirectory;

	public ByteArrayInputStream createPdf(java.util.List<NotebookEntryState> notebookEntries) throws CreatePdfException {
		try {
			if (notebookEntries.isEmpty()) {
				throw new CreatePdfException("Notebook has no pages");
			}

			registerFonts();

			Rectangle pageSize = new Rectangle(PageSize.A4);
			// set document background color
			pageSize.setBackgroundColor(new BaseColor(242, 240, 238));
			Document document = new Document(pageSize);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			PdfWriter.getInstance(document, out);
			document.open();

			for (NotebookEntryState notebookEntryState : notebookEntries) {
				// Title
				document.add(getTitleStyle(notebookEntryState.getNotebookEntry().getTitle()));
				document.add(Chunk.NEWLINE);
				// Content
				for (NotebookContent notebookContent : notebookEntryState.getNotebookContent()) {
					Component component = notebookContent.getComponent();

					if (component instanceof ImageComponent) {
						addImageComponent(component, document);
					} else if (component instanceof VideoComponent) {
						addVideoComponent(component, document);
					} else if (component instanceof TextComponent) {
						addText(((TextComponent) component).getMarkdownContent(), document);
					} else if (component instanceof AudioComponent) {
						addAudioComponent(component, document);
					} else if (component instanceof TextExercise) {
						addText(notebookContent.getExerciseState().getInputState(), document);
					} else if (component instanceof RadioButtonExercise) {
						addListExercise(((RadioButtonExercise) notebookContent.getComponent()).getOptions(), notebookContent, document);
					} else if (component instanceof DateExercise) {
						addText(notebookContent.getExerciseState().getInputState(), document);
					} else if (component instanceof UploadExercise) {
						addUploadExercise(notebookContent, document);
					} else if (component instanceof CheckboxExercise) {
						addListExercise(((CheckboxExercise) notebookContent.getComponent()).getOptions(), notebookContent, document);
					} else if (component instanceof LinkComponent) {
						addLinkComponent(component, document);
					}

					if (notebookContent.getPersonalNote() != null) {
						addPersonalNote(notebookContent.getPersonalNote(), document);
					}
				}

				document.add(Chunk.NEXTPAGE);
			}

			document.close();

			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException | DocumentException e) {
			throw new CreatePdfException(e.getMessage());
		}

	}

	private void addLinkComponent(Component component, Document document) throws IOException, DocumentException {
		LinkComponent linkComponent = (LinkComponent) component;
		Path iconPath = Paths.get(resourceStaticDirectory + File.separator + linkComponent.getIcon().getThumbnailPath());
		Image icon = getImageStyle(iconPath.toString());
		Paragraph linkText = getTextStyle(linkComponent.getLinkText());
		linkText.setAlignment(Element.ALIGN_CENTER);
		icon.scaleToFit(new Rectangle(50, 100));

		document.add(icon);
		document.add(linkText);
		document.add(Chunk.NEWLINE);
	}

	private void addUploadExercise(NotebookContent notebookContent, Document document) throws DocumentException {
		Path filePath = Paths.get(UploadExerciseService.getUploadDirectory() + notebookContent.getExerciseState().getInputState());
		addText(filePath.getFileName().toString().replaceAll("[^a-zA-Z.-]-*", ""), document);
	}

	private void addListExercise(List<ExerciseOption> exerciseOptionList, NotebookContent notebookContent, Document document) throws DocumentException {
		PdfPTable myTable = new PdfPTable(4);
		myTable.setWidthPercentage(100.0f);
		myTable.setHorizontalAlignment(Element.ALIGN_LEFT);

		int index = 0;
		for (ExerciseOption exerciseOption : exerciseOptionList) {
			Chunk text = new Chunk(exerciseOption.getText(), FontFactory.getFont("LibreItalic", textFontSize, BaseColor.BLACK));
			if (false == notebookContent.getExerciseState().getInputState().contains(Integer.toString(index))) {
				text.setUnderline(1.5f, 3.5f);
			}
			PdfPCell cell = new PdfPCell();
			cell.setPhrase(new Phrase(text));
			cell.setBorder(Rectangle.NO_BORDER);
			myTable.addCell(cell);
			index++;
		}

		document.add(myTable);
		document.add(Chunk.NEWLINE);
	}

	private void addAudioComponent(Component component, Document document) throws DocumentException {
		AudioComponent audioComponent = (AudioComponent) component;
		Path audioPath = Paths.get(resourceStaticDirectory + File.separator + "audio" + File.separator + audioComponent.getAudio().getPath());
		Paragraph audioName = getTextStyle(audioPath.getFileName().toString());
		audioName.setAlignment(Element.ALIGN_CENTER);
		document.add(audioName);
		document.add(Chunk.NEWLINE);
	}

	private void addImageComponent(Component component, Document document) throws IOException, DocumentException {
		ImageComponent imageComponent = (ImageComponent) component;
		for (ImageResource imageResource : imageComponent.getImages()) {
			Path path = Paths.get(resourceStaticDirectory + File.separator + imageResource.getPath());
			document.add(getImageStyle(path.toFile().getAbsolutePath()));
		}

		document.add(getCaptionStyle(imageComponent.getCaption()));
		document.add(Chunk.NEWLINE);
	}

	private void addText(String text, Document document) throws DocumentException {
		document.add(getTextStyle(text.replaceAll("<br>", "\n")));
		document.add(Chunk.NEWLINE);
	}

	private void addVideoComponent(Component component, Document document) throws DocumentException {
		VideoComponent videoComponent = (VideoComponent) component;
		Path videoPath = Paths.get(resourceStaticDirectory + File.separator + videoComponent.getVideo().getPath());
		Paragraph videoName = getTextStyle(videoPath.getFileName().toString());
		videoName.setAlignment(Element.ALIGN_CENTER);
		Paragraph caption = getCaptionStyle(videoComponent.getCaption());
		caption.setAlignment(Element.ALIGN_CENTER);

		document.add(Chunk.NEWLINE);
		document.add(videoName);
		document.add(Chunk.NEWLINE);
		document.add(caption);
		document.add(Chunk.NEWLINE);
	}

	private void addPersonalNote(PersonalNote personalNote, Document document) throws DocumentException {
		PdfPTable myTable = new PdfPTable(1);
		myTable.setWidthPercentage(100.0f);
		myTable.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell cellOne = new PdfPCell(new Paragraph(personalNote.getCreatedAt(), FontFactory.getFont("LibreRegular", textFontSize, personalNoteFontColor)));
		PdfPCell cellTwo = new PdfPCell(getPersonalNoteStyle(personalNote));
		cellOne.setBorder(Rectangle.LEFT);
		cellOne.setPaddingLeft(20);
		cellTwo.setBorder(Rectangle.LEFT);
		cellTwo.setPaddingLeft(20);
		myTable.addCell(cellOne);
		myTable.addCell(cellTwo);

		document.add(myTable);
		document.add(Chunk.NEWLINE);
	}

	private void registerFonts() {
//		FontFactory.register(resourceDirectory + File.separator + "fonts" + File.separator + "LibreBaskerville-Regular.ttf", "LibreRegular");
//		FontFactory.register(resourceDirectory + File.separator + "fonts" + File.separator + "LibreBaskerville-Italic.ttf", "LibreItalic");
//		FontFactory.register(resourceDirectory + File.separator + "fonts" + File.separator + "LibreBaskerville-Bold.ttf", "LibreBold");
	}

	private Paragraph getTitleStyle(String title) {
		return new Paragraph(title, FontFactory.getFont("LibreBold", titleFontSize, titleFontColor));
	}

	private Paragraph getTextStyle(String text) {
		Paragraph p = new Paragraph(text, FontFactory.getFont("LibreRegular", textFontSize, textFontColor));
		p.setAlignment(Element.ALIGN_JUSTIFIED);
		return p;
	}

	private Paragraph getCaptionStyle(String text) {
		return new Paragraph(text, FontFactory.getFont("LibreItalic", captionFontSize, captionFontColor));
	}

	private Paragraph getPersonalNoteStyle(PersonalNote personalNote) {
		Paragraph p = new Paragraph(personalNote.getMarkdownContent(), FontFactory.getFont("LibreItalic", textFontSize, personalNoteFontColor));
		p.setAlignment(Element.ALIGN_JUSTIFIED);
		return p;
	}

	private Image getImageStyle (String imagePath) throws BadElementException, IOException {
		Image image = Image.getInstance(imagePath);
		image.scaleToFit(500, 300);
		image.setRotationDegrees(3);
		//	    image.scalePercent(40f);
		image.setAlignment(Element.ALIGN_CENTER);
		image.setBorder(Image.BOX);
		image.setBorderWidth(20);
		image.setBorderColor(BaseColor.WHITE);

		return image;
	}
}
