package ch.uzh.marugoto.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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

    @Value("${marugoto.resource.static.dir}")
    protected String resourceStaticDirectory;
    @Value("${marugoto.resource.temp.dir}")
    protected String resourceDirectory;
    protected PdfWriter pdfWriter;

    /**
     * Pdf creation logic
     *
     * @param notebookEntries
     * @return
     * @throws CreatePdfException
     */
    public ByteArrayInputStream createPdf(java.util.List<NotebookEntryState> notebookEntries) throws CreatePdfException {
        try {
            if (notebookEntries.isEmpty()) {
                throw new CreatePdfException("Notebook has no pages");
            }


            PdfStylingService.registerFonts(resourceDirectory + File.separator + "fonts");

            Rectangle pageSize = new Rectangle(PageSize.A5);
            Document document = new Document(pageSize);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            pdfWriter = PdfWriter.getInstance(document, out);
            document.open();

            for (NotebookEntryState notebookEntryState : notebookEntries) {
                // Title
                document.add(PdfStylingService.getTitleStyle(notebookEntryState.getNotebookEntry().getTitle()));
                document.add(Chunk.NEWLINE);
                // Content
                for (NotebookContent notebookContent : notebookEntryState.getNotebookContent()) {
                    Component component = notebookContent.getComponent();

                    if (component instanceof ImageComponent) {
                        addImageComponent(component, document);
                    } else if (component instanceof VideoComponent) {
                        //addVideoComponent(component, document);
                    } else if (component instanceof TextComponent) {
                        addText(((TextComponent) component).getMarkdownContent(), document);
                    } else if (component instanceof AudioComponent) {
                        //addAudioComponent(component, document);
                    } else if (component instanceof TextExercise) {
                        addText("My answer\n" + notebookContent.getExerciseState().getInputState(), document);
                    } else if (component instanceof RadioButtonExercise) {
                        addListExercise(((RadioButtonExercise) notebookContent.getComponent()).getOptions(), notebookContent, document);
                    } else if (component instanceof DateExercise) {
                        addText("My answer\n" + notebookContent.getExerciseState().getInputState(), document);
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

    /**
     * Pdf Link component
     *
     * @param component
     * @param document
     * @throws IOException
     * @throws DocumentException
     */
    private void addLinkComponent(Component component, Document document) throws IOException, DocumentException {
        LinkComponent linkComponent = (LinkComponent) component;
        Path iconPath = Paths.get(resourceStaticDirectory + File.separator + linkComponent.getIcon().getThumbnailPath());
        Image icon = PdfStylingService.getImageStyle(iconPath.toString());
        Paragraph linkText = PdfStylingService.getTextStyle(linkComponent.getLinkText());
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

    /**
     * Pdf List exercise component
     *
     * @param exerciseOptionList
     * @param notebookContent
     * @param document
     * @throws DocumentException
     */
    private void addListExercise(List<ExerciseOption> exerciseOptionList, NotebookContent notebookContent, Document document) throws DocumentException {
        PdfPTable myTable = new PdfPTable(4);
        myTable.setWidthPercentage(100.0f);
        myTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        int index = 0;
        for (ExerciseOption exerciseOption : exerciseOptionList) {
            Chunk text = PdfStylingService.getListStyle(exerciseOption.getText());
            // check if it should be crossed
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

    /**
     * Pdf Audio component
     *
     * @param component
     * @param document
     * @throws DocumentException
     */
    private void addAudioComponent(Component component, Document document) throws DocumentException {
        AudioComponent audioComponent = (AudioComponent) component;
        Path audioPath = Paths.get(resourceStaticDirectory + File.separator + "audio" + File.separator + audioComponent.getAudio().getPath());
        Paragraph audioName = PdfStylingService.getTextStyle(audioPath.getFileName().toString());
        audioName.setAlignment(Element.ALIGN_CENTER);
        document.add(audioName);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Pdf Image component
     *
     * @param component
     * @param document
     * @throws IOException
     * @throws DocumentException
     */
    private void addImageComponent(Component component, Document document) throws IOException, DocumentException {
        ImageComponent imageComponent = (ImageComponent) component;
        for (ImageResource imageResource : imageComponent.getImages()) {
            Path path = Paths.get(resourceStaticDirectory + File.separator + imageResource.getPath());
            document.add(PdfStylingService.getImageStyle(path.toFile().getAbsolutePath()));
        }

        document.add(PdfStylingService.getCaptionStyle(imageComponent.getCaption()));
        document.add(Chunk.NEWLINE);
    }

    /**
     * Pdf Text component
     *
     * @param text
     * @param document
     * @throws DocumentException
     */
    private void addText(String text, Document document) throws DocumentException {
        var paragraph = PdfStylingService.getTextStyle(text.replaceAll("<br>", "\n"));
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(paragraph);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Pdf Video component
     *
     * @param component
     * @param document
     * @throws DocumentException
     */
    private void addVideoComponent(Component component, Document document) throws DocumentException {
        VideoComponent videoComponent = (VideoComponent) component;
        Path videoPath = Paths.get(resourceStaticDirectory + File.separator + videoComponent.getVideo().getPath());
        Paragraph videoName = PdfStylingService.getTextStyle(videoPath.getFileName().toString());
        videoName.setAlignment(Element.ALIGN_CENTER);
        Paragraph caption = PdfStylingService.getCaptionStyle(videoComponent.getCaption());
        caption.setAlignment(Element.ALIGN_CENTER);

        document.add(Chunk.NEWLINE);
        document.add(videoName);
        document.add(Chunk.NEWLINE);
        document.add(caption);
        document.add(Chunk.NEWLINE);
    }

    /**
     * Pdf Personal Note component
     * @param personalNote
     * @param document
     * @throws DocumentException
     */
    private void addPersonalNote(PersonalNote personalNote, Document document) throws DocumentException {
        PdfPTable myTable = new PdfPTable(1);
        myTable.setWidthPercentage(100.0f);
        myTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        //
        PdfPCell cellOne = new PdfPCell(PdfStylingService.getPersonalNoteDateStyle(personalNote));
        PdfPCell cellTwo = new PdfPCell(PdfStylingService.getPersonalNoteStyle(personalNote));
        cellOne.setBorder(Rectangle.LEFT);
        cellOne.setPaddingLeft(20);
        cellTwo.setBorder(Rectangle.LEFT);
        cellTwo.setPaddingLeft(20);
        myTable.addCell(cellOne);
        myTable.addCell(cellTwo);

        document.add(myTable);
        document.add(Chunk.NEWLINE);
    }
}
