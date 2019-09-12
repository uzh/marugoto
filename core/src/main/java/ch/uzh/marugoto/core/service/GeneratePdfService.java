package ch.uzh.marugoto.core.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import ch.uzh.marugoto.core.data.entity.state.GameState;
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
    private PdfWriter pdfWriter;
    private GameState gameState;
    private Document document;


    private class HeaderFooterPageEvent extends PdfPageEventHelper {
        public void onStartPage(PdfWriter pdfWriter, Document document) {}

        public void onEndPage(PdfWriter pdfWriter, Document document) {
            // Left footer text
            var userFullName = gameState.getUser().getName();
            Phrase footerLeftText = PdfStylingService.getFooterStyle(userFullName);
            float footerLeftTextWidth = footerLeftText.getContent().length() * 3;
            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER, footerLeftText, footerLeftTextWidth + PdfStylingService.MARGIN_LEFT, PdfStylingService.MARGIN_BOTTOM - 10, 0);
            // Right footer text
            var topicName = gameState.getTopic().getTitle();
            var pageNumber = " - Page ".concat(String.valueOf(pdfWriter.getPageNumber()));
            Phrase footerRightText = PdfStylingService.getFooterStyle(topicName.concat(pageNumber));
            float footerRightTextWidth = footerRightText.getContent().length() * 3;
            ColumnText.showTextAligned(pdfWriter.getDirectContent(), Element.ALIGN_CENTER, footerRightText, PageSize.A5.getWidth() - footerRightTextWidth, PdfStylingService.MARGIN_BOTTOM - 10, 0);
        }
    }

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

            gameState = notebookEntries.get(0).getGameState();

            Rectangle pageSize = new Rectangle(PageSize.A5);
            document = new Document(pageSize, PdfStylingService.MARGIN_LEFT, PdfStylingService.MARGIN_RIGHT, PdfStylingService.MARGIN_TOP, PdfStylingService.MARGIN_BOTTOM);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            HashMap<String, ImageComponent> appendixImages = new HashMap<>();

            pdfWriter = PdfWriter.getInstance(document, out);
            HeaderFooterPageEvent event = new HeaderFooterPageEvent();
            document.addTitle(gameState.getUser().getName().concat(" - Notebook"));
            document.open();
            addInfoPageContent();
            pdfWriter.setPageEvent(event);


            for (NotebookEntryState notebookEntryState : notebookEntries) {
                if (notebookEntryState.getNotebookContent().size() > 0) {
                    // Title
                    addTitleText(notebookEntryState.getNotebookEntry().getTitle());
                    // Content
                    for (NotebookContent notebookContent : notebookEntryState.getNotebookContent()) {
                        Component component = notebookContent.getComponent();

                        if (component instanceof ImageComponent) {
                            ImageComponent imageComponent = (ImageComponent) component;

                            if (imageComponent.isZoomable()) {
                                appendixImages.put(notebookEntryState.getTitle(), imageComponent);
                            }

                            addImageComponent(imageComponent, document);
                        } else if (component instanceof VideoComponent) {
                            //addVideoComponent(component, document);
                        } else if (component instanceof TextComponent) {
                            addText(((TextComponent) component).getMarkdownContent());
                        } else if (component instanceof AudioComponent) {
                            //addAudioComponent(component, document);
                        } else if (component instanceof TextExercise) {
                            addUnderlineText("My input");
                            addText(notebookContent.getExerciseState().getInputState());
                        } else if (component instanceof RadioButtonExercise) {
                            addListExercise(((RadioButtonExercise) notebookContent.getComponent()).getOptions(), notebookContent);
                        } else if (component instanceof DateExercise) {
                            addUnderlineText("My input");
                            addText(notebookContent.getExerciseState().getInputState());
                        } else if (component instanceof UploadExercise) {
                            addUploadExercise(notebookContent);
                        } else if (component instanceof CheckboxExercise) {
                            addListExercise(((CheckboxExercise) notebookContent.getComponent()).getOptions(), notebookContent);
                        } else if (component instanceof LinkComponent) {
                            addLinkComponent(component);
                        }
                        if (notebookContent.getPersonalNote() != null) {
                            addPersonalNote(notebookContent.getPersonalNote());
                        }
                    }

                    document.newPage();
                }
            }

            // appendix of images
            addAppendix(appendixImages);
            document.close();

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException | DocumentException e) {
            throw new CreatePdfException(e.getMessage());
        }

    }

    private void addInfoPageContent() throws DocumentException {
        var title = new Paragraph(gameState.getTopic().getTitle(), PdfStylingService.getH1(PdfStylingService.FontStyle.Bold));
        title.setAlignment(Element.ALIGN_CENTER);
        title.add(Chunk.NEWLINE);
        title.add(Chunk.NEWLINE);
        title.add(Chunk.NEWLINE);
        title.add(Chunk.NEWLINE);
        document.add(title);

        var title2 = new Paragraph("Notebook", PdfStylingService.getH2(PdfStylingService.FontStyle.Regular));
        title2.setAlignment(Element.ALIGN_CENTER);
        title2.add(Chunk.NEWLINE);
        title2.add("-");
        title2.add(Chunk.NEWLINE);
        title2.add(gameState.getUser().getName());
        title2.add(Chunk.NEWLINE);
        document.add(title2);

        var subtitle = new Paragraph(gameState.getStartedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), PdfStylingService.getH2(PdfStylingService.FontStyle.Italic));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.add(" - ");
        var finishedAt = gameState.getFinishedAt() != null ? gameState.getFinishedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "not finished";
        subtitle.add(finishedAt);

        document.add(subtitle);
        document.newPage();
    }

    private void addTitleText(String text) throws DocumentException {
        text = text.replaceAll("<br>", "\n");
        var paragraph = new Paragraph(text, PdfStylingService.getTitleStyle());
        document.add(paragraph);
        addHorizontalLine();
    }

    private void addUnderlineText(String text) throws DocumentException {
        Chunk chunkText = PdfStylingService.getListStyle(text);
        chunkText.setUnderline(1f, -8);
        Paragraph p = new Paragraph();
        p.add(chunkText);
        p.setIndentationLeft(PdfStylingService.MARGIN_LEFT);
        document.add(p);
        document.add(Chunk.NEWLINE);
    }

    private void addAppendix(HashMap<String, ImageComponent> appendixImages) throws DocumentException, IOException {
        addTitleText("Appendix of images");
        for (Map.Entry<String, ImageComponent> entry : appendixImages.entrySet()) {
            addText(entry.getKey());
            addAppendixImage(entry.getValue(), document);
            document.add(Chunk.NEXTPAGE);
        }
    }

    /**
     * Pdf Link component
     *
     * @param component
     * @throws IOException
     * @throws DocumentException
     */
    private void addLinkComponent(Component component) throws IOException, DocumentException {
        LinkComponent linkComponent = (LinkComponent) component;
        Path iconPath = Paths.get(resourceStaticDirectory + File.separator + linkComponent.getIcon().getThumbnailPath());
        Image icon = PdfStylingService.getImageStyle(iconPath.toString());
        Paragraph linkText = new Paragraph(linkComponent.getLinkText(), PdfStylingService.getTextStyle());
        linkText.setAlignment(Element.ALIGN_CENTER);
        icon.scaleToFit(new Rectangle(50, 100));

        document.add(icon);
        document.add(linkText);
        document.add(Chunk.NEWLINE);
    }

    private void addUploadExercise(NotebookContent notebookContent) throws DocumentException {
        Path filePath = Paths.get(UploadExerciseService.getUploadDirectory() + notebookContent.getExerciseState().getInputState());
        addText(filePath.getFileName().toString().replaceAll("[^a-zA-Z.-]-*", ""));
    }

    /**
     * Pdf List exercise component
     *
     * @param exerciseOptionList
     * @param notebookContent
     * @throws DocumentException
     */
    private void addListExercise(List<ExerciseOption> exerciseOptionList, NotebookContent notebookContent) throws DocumentException {
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
     * Pdf Image component
     * if there are multiple images only first will be added
     *
     * @param imageComponent
     * @param document
     * @throws IOException
     * @throws DocumentException
     */
    private void addImageComponent(ImageComponent imageComponent, Document document) throws IOException, DocumentException {
        ImageResource imageResource = imageComponent.getImages().get(0);
        Path path = Paths.get(resourceStaticDirectory + File.separator + imageResource.getPath());

        document.add(PdfStylingService.getImageStyle(path.toFile().getAbsolutePath()));
        document.add(new Paragraph(imageComponent.getCaption(), PdfStylingService.getCaptionStyle()));
        document.add(Chunk.NEWLINE);
    }

    /**
     * if there are multiple images only first will be added
     *
     * @param imageComponent
     * @param document
     * @throws IOException
     * @throws DocumentException
     */
    private void addAppendixImage(ImageComponent imageComponent, Document document) throws IOException, DocumentException {
        ImageResource imageResource = imageComponent.getImages().get(0);

        Path path = Paths.get(resourceStaticDirectory + File.separator + imageResource.getPath());
        Image image = Image.getInstance(path.toFile().getAbsolutePath());
        var imageWidth = pdfWriter.getPageSize().getWidth() - PdfStylingService.MARGIN_LEFT * 2;
        var imageHeight = imageWidth / (image.getWidth() / image.getHeight());
        image.scaleToFit(imageWidth, imageHeight);
        image.setAlignment(Element.ALIGN_CENTER);
        document.add(image);

        document.add(new Paragraph(imageComponent.getCaption(), PdfStylingService.getCaptionStyle()));
        document.add(Chunk.NEWLINE);
    }

    /**
     * Pdf Text component
     *
     * @param text
     * @throws DocumentException
     */
    private void addText(String text) throws DocumentException {
        var paragraph = new Paragraph(text.replaceAll("<br>", "\n"), PdfStylingService.getTextStyle());
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        paragraph.setIndentationLeft(PdfStylingService.MARGIN_LEFT);
        document.add(paragraph);
        document.add(Chunk.NEWLINE);
    }

    private void addHorizontalLine() throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(new LineSeparator(0.5f, 100, BaseColor.GRAY, Element.ALIGN_BASELINE, 0));
        document.add(Chunk.NEWLINE);
    }

    /**
     * Pdf Personal Note component
     *
     * @param personalNote
     * @throws DocumentException
     */
    private void addPersonalNote(PersonalNote personalNote) throws DocumentException {
        PdfPTable myTable = new PdfPTable(1);
        myTable.setTotalWidth(pdfWriter.getPageSize().getWidth());

        Paragraph dateText = new Paragraph(personalNote.getCreatedAt(), PdfStylingService.getPersonalNoteDateStyle());
        PdfPCell cellOne = new PdfPCell(dateText);
        cellOne.setBorder(Rectangle.LEFT);
        cellOne.setPaddingLeft(20);

        Paragraph noteText = new Paragraph(personalNote.getMarkdownContent(), PdfStylingService.getPersonalNoteStyle());
        noteText.setAlignment(Element.ALIGN_JUSTIFIED);
        PdfPCell cellTwo = new PdfPCell(noteText);
        cellTwo.setBorder(Rectangle.LEFT);
        cellTwo.setPaddingLeft(20);

        myTable.addCell(cellOne);
        myTable.addCell(cellTwo);

        PdfContentByte canvas = pdfWriter.getDirectContent();
        myTable.writeSelectedRows(0, 2, PdfStylingService.MARGIN_LEFT * 2, pdfWriter.getVerticalPosition(true), canvas);
        document.add(Chunk.NEWLINE);
    }
}
