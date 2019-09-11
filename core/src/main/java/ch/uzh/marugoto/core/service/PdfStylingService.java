package ch.uzh.marugoto.core.service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;

import java.io.File;
import java.io.IOException;

import ch.uzh.marugoto.core.data.entity.state.PersonalNote;

public class PdfStylingService {
    public static final BaseColor BACKGROUND_COLOR = new BaseColor(236, 234, 232, 1);
    public static final int MARGIN_LEFT = 20;
    public static final int MARGIN_RIGHT = 20;
    public static final int MARGIN_TOP = 30;
    public static final int MARGIN_BOTTOM = 30;
    public static final int H1 = 20;
    public static final int H2 = 15;
    public static final int H3 = 13;
    public static final int P = 11;
    public static final int SMALL = 9;
    public static final int CAPTION = 8;
    public static final int textFontSize = 11;
    public static final int captionFontSize = 8;
    public static final int IMAGE_WIDTH = 350;
    public static final int IMAGE_HEIGHT = 150;
    public static final int IMAGE_APPENDIX_WIDTH = 480;
    public static final int IMAGE_APPENDIX_HEIGHT = 205;
    public static final int IMAGE_ROTATION = 3;
    public static final BaseColor titleFontColor = BaseColor.BLACK;
    public static final BaseColor textFontColor = BaseColor.DARK_GRAY;
    public static final BaseColor captionFontColor = BaseColor.GRAY;
    public static final BaseColor personalNoteFontColor = BaseColor.BLACK;

    public enum FontStyle {
        Regular, Bold, Italic
    }


    public static void registerFonts(String fontsDirectory) {
        FontFactory.register(fontsDirectory + File.separator + "LibreBaskerville-Regular.ttf", "LibreRegular");
        FontFactory.register(fontsDirectory + File.separator + "LibreBaskerville-Italic.ttf", "LibreItalic");
        FontFactory.register(fontsDirectory + File.separator + "LibreBaskerville-Bold.ttf", "LibreBold");
    }

    public static Font getFont(FontStyle fontStyle) {
        Font font;
        switch (fontStyle) {
            case Italic:
                font = FontFactory.getFont(FontFactory.TIMES_ITALIC);
                break;
            case Bold:
                font = FontFactory.getFont(FontFactory.TIMES_BOLD);
                break;
            default:
                font = FontFactory.getFont(FontFactory.TIMES);
        }
        return font;
    }

    public static Font getTitleStyle() {
        Font titleFont = getFont(FontStyle.Regular);
        titleFont.setSize(H3);
        titleFont.setColor(titleFontColor);
        return titleFont;
    }

    public static Font getH1(FontStyle fontStyle) {
        Font h1 = getFont(fontStyle);
        h1.setSize(PdfStylingService.H1);
        return h1;
    }

    public static Font getH2(FontStyle fontStyle) {
        Font h2 = getFont(fontStyle);
        h2.setSize(PdfStylingService.H2);
        return h2;
    }

    public static Paragraph getTextStyle(String text) {
        Font font = getFont(FontStyle.Regular);
        font.setSize(P);
        font.setColor(textFontColor);
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        return p;
    }

    public static Phrase getFooterStyle(String text) {
        FontSelector selector = new FontSelector();
        Font f1 = getFont(FontStyle.Regular);
        f1.setColor(BaseColor.GRAY);
        f1.setSize(SMALL);
        selector.addFont(f1);
        return selector.process(text);
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

    public static Paragraph getCaptionStyle(String text) {
        Font font = getFont(FontStyle.Italic);
        font.setSize(CAPTION);
        font.setColor(captionFontColor);
        return new Paragraph(text, font);
    }

    public static Paragraph getPersonalNoteStyle(PersonalNote personalNote) {
        Font font = getFont(FontStyle.Italic);
        font.setSize(P);
        font.setColor(personalNoteFontColor);
        var p =  new Paragraph(personalNote.getMarkdownContent(), font);
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        return p;
    }

    public static Paragraph getPersonalNoteDateStyle(PersonalNote personalNote) {
        Font font = getFont(FontStyle.Regular);
        font.setSize(P);
        font.setColor(personalNoteFontColor);
        return new Paragraph(personalNote.getCreatedAt(), font);
    }

    public static Chunk getListStyle(String text) {
        Font font = getFont(FontStyle.Italic);
        font.setSize(P);
        font.setColor(BaseColor.BLACK);
        return new Chunk(text, font);
    }
}
