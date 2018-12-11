package ch.uzh.marugoto.core.service;

import com.google.common.io.Files;

import org.apache.logging.log4j.core.util.FileUtils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageService {

    public static final int MAX_COLUMNS = 12;
    public static final int MAX_WIDTH = 1600;

    public static Image readImage(String imageUrl) throws IOException {
        File file = new File(imageUrl);
        return ImageIO.read(file);
    }

    public static Image readImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static File resizeImage(File imageFile, int width) throws IOException {
        // read image from file
        BufferedImage bufferedImage = (BufferedImage) readImage(imageFile);
        var bWidth = (double) bufferedImage.getWidth();
        var bHeight = (double) bufferedImage.getHeight();
        var height =(int) Math.round(bHeight / bWidth * width);
        Image tmp = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        // resize image
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(tmp, 0, 0, null);
        graphics2D.dispose();
        // write to file
        var extension = Files.getFileExtension(imageFile.getName());
        var resizedImageName = String.format(imageFile.getParentFile().getAbsolutePath() + File.separator + Files.getNameWithoutExtension(imageFile.getName()) + " - resized %d x %d", width, height);
        imageFile = new File(resizedImageName + "." + extension);
        ImageIO.write(resizedImage, extension, imageFile);
        return imageFile;
    }
}
