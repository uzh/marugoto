package ch.uzh.marugoto.core.service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;

import java.io.File;
import java.io.IOException;

import ch.uzh.marugoto.core.data.entity.state.PersonalNote;

public class PdfStylingService {
    public static final int titleFontSize = 12;
    public static final int textFontSize = 10;
    public static final int captionFontSize = 8;
    public static final int IMAGE_WIDTH = 350;
    public static final int IMAGE_HEIGHT = 150;
    public static final int IMAGE_APPENDIX_WIDTH = 480;
    public static final int IMAGE_APPENDIX_HEIGHT = 205;
    public static final int IMAGE_ROTATION = 3;
    public static final BaseColor titleFontColor = BaseColor.DARK_GRAY;
    public static final BaseColor textFontColor = BaseColor.DARK_GRAY;
    public static final BaseColor captionFontColor = BaseColor.GRAY;
    public static final BaseColor personalNoteFontColor = BaseColor.BLACK;


    public static void registerFonts(String fontsDirectory) {
        FontFactory.register(fontsDirectory + File.separator + "LibreBaskerville-Regular.ttf", "LibreRegular");
        FontFactory.register(fontsDirectory + File.separator + "LibreBaskerville-Italic.ttf", "LibreItalic");
        FontFactory.register(fontsDirectory + File.separator + "LibreBaskerville-Bold.ttf", "LibreBold");
    }

    public static Paragraph getTitleStyle(String title) {
        return new Paragraph(title, FontFactory.getFont("LibreBold", titleFontSize, titleFontColor));
    }

    public static Paragraph getTextStyle(String text) {
        Paragraph p = new Paragraph(text, FontFactory.getFont("LibreRegular", textFontSize, textFontColor));
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        return p;
    }

    public static Image getImageStyle (String imagePath) throws BadElementException, IOException {
        Image image = Image.getInstance(imagePath);
        image.scaleToFit(IMAGE_WIDTH, IMAGE_HEIGHT);
        image.setRotationDegrees(IMAGE_ROTATION);
        image.setAlignment(Element.ALIGN_CENTER);
        image.setBorder(Image.BOX);
        image.setBorderWidth(20);
        image.setBorderColor(BaseColor.WHITE);

        return image;
    }

    public static Image getAppendixImageStyle (String imagePath) throws BadElementException, IOException {
        Image image = Image.getInstance(imagePath);
        image.scaleToFit(IMAGE_APPENDIX_WIDTH, IMAGE_APPENDIX_HEIGHT);
        image.setAlignment(Element.ALIGN_CENTER);

        return image;
    }

    public static Paragraph getCaptionStyle(String text) {
        return new Paragraph(text, FontFactory.getFont("LibreItalic", captionFontSize, captionFontColor));
    }

    public static Paragraph getPersonalNoteStyle(PersonalNote personalNote) {
        Paragraph p = new Paragraph(personalNote.getMarkdownContent(), FontFactory.getFont("LibreItalic", textFontSize, personalNoteFontColor));
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        return p;
    }

    public static Paragraph getPersonalNoteDateStyle(PersonalNote personalNote) {
        return new Paragraph(personalNote.getCreatedAt(), FontFactory.getFont("LibreRegular", textFontSize, personalNoteFontColor));
    }

    public static Chunk getListStyle(String text) {
        return new Chunk(text, FontFactory.getFont("LibreItalic", textFontSize, BaseColor.BLACK));
    }
}
