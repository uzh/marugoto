package ch.uzh.marugoto.core.service;

import java.io.IOException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.FontSelector;


public class PdfStylingService {
    public static final BaseColor BACKGROUND_COLOR = new BaseColor(236, 234, 232);
    public static final int MARGIN_LEFT = 20;
    public static final int MARGIN_RIGHT = 20;
    public static final int MARGIN_TOP = 30;
    public static final int MARGIN_BOTTOM = 30;
    public static final int H1 = 20;
    public static final int H2 = 17;
    public static final int H3 = 15;
    public static final int H4 = 13;
    public static final int P = 11;
    public static final int SMALL = 9;
    public static final int CAPTION = 8;
    public static final float IMAGE_SCALE = 0.6f;
    public static final float APPENDIX_IMAGE_SCALE = 0.8f;
    public static final BaseColor titleFontColor = BaseColor.BLACK;
    public static final BaseColor textFontColor = BaseColor.DARK_GRAY;
    public static final BaseColor captionFontColor = BaseColor.GRAY;
    public static final BaseColor personalNoteFontColor = BaseColor.BLACK;

    public enum FontStyle {
        Regular, Bold, Italic
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

    public static Font getH3(FontStyle fontStyle) {
        Font h3 = getFont(fontStyle);
        h3.setSize(PdfStylingService.H3);
        return h3;
    }

    public static Font getH4(FontStyle fontStyle) {
        Font h4 = getFont(fontStyle);
        h4.setSize(PdfStylingService.H4);
        return h4;
    }

    public static Font getP(FontStyle fontStyle) {
        Font p = getFont(fontStyle);
        p.setSize(PdfStylingService.P);
        return p;
    }

    public static Font getTextStyle() {
        return getTextStyle(FontStyle.Regular);
    }

    public static Font getTextStyle(FontStyle fontStyle) {
        Font font = getFont(fontStyle);
        font.setSize(P);
        font.setColor(textFontColor);
        return font;
    }

    public static Phrase getFooterStyle(String text) {
        FontSelector selector = new FontSelector();
        Font f1 = getFont(FontStyle.Regular);
        f1.setColor(BaseColor.GRAY);
        f1.setSize(SMALL);
        selector.addFont(f1);
        return selector.process(text);
    }

    public static Image getImageStyle(String imagePath, float pageWidth) throws BadElementException, IOException {
        Image image = Image.getInstance(imagePath);
        var imageWidth = pageWidth * IMAGE_SCALE - PdfStylingService.MARGIN_LEFT * 2;
        var imageHeight = imageWidth / (image.getWidth() / image.getHeight());
        image.scaleToFit(imageWidth, imageHeight);
        image.setAlignment(Element.ALIGN_CENTER);
        image.setBorder(Image.BOX);
        image.setBorderWidth(20);
        image.setBorderColor(BaseColor.WHITE);

        return image;
    }

    public static Image getAppendixImageStyle(String imagePath, float pageWidth) throws IOException, BadElementException {
        Image image = Image.getInstance(imagePath);
        var imageWidth = pageWidth * APPENDIX_IMAGE_SCALE - PdfStylingService.MARGIN_LEFT * 2;
        var imageHeight = imageWidth / (image.getWidth() / image.getHeight());
        image.scaleToFit(imageWidth, imageHeight);
        image.setAlignment(Element.ALIGN_CENTER);
        return image;
    }

    public static Font getCaptionStyle() {
        Font font = getFont(FontStyle.Italic);
        font.setSize(CAPTION);
        font.setColor(captionFontColor);
        return font;
    }

    public static Font getPersonalNoteStyle() {
        Font font = getFont(FontStyle.Italic);
        font.setSize(P);
        font.setColor(personalNoteFontColor);
        return font;
    }

    public static Font getPersonalNoteDateStyle() {
        Font font = getFont(FontStyle.Regular);
        font.setSize(P);
        font.setColor(personalNoteFontColor);
        return font;
    }

    public static Chunk getListStyle(String text) {
        Font font = getP(FontStyle.Italic);
        font.setColor(BaseColor.BLACK);
        return new Chunk(text, font);
    }
}
