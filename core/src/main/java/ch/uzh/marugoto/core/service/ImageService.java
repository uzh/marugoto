package ch.uzh.marugoto.core.service;

import com.google.common.io.Files;

import org.springframework.stereotype.Service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;

@Service
public class ImageService extends ResourceService {

    private static final int GRID_COLUMNS = 12;
    private static final int CONTAINER_WIDTH = 1600;
    private static final int RESOLUTION_MULTIPLIER = 2;

    public static int getImageWidth(String imagePath) throws IOException {
        return ImageIO.read(new File(imagePath)).getWidth();
    }

    private static ImageResource getImageResource(String imagePath) throws ResourceNotFoundException {
        return (ImageResource) getFromPath(imagePath);
    }

    /**
     * Calculates image width from number of grid columns
     *
     * @param imagePath
     * @param columns
     * @return
     * @throws IOException
     */
    public static int getImageWidthForColumns(String imagePath, int columns) throws IOException {
        var imageWidth = getImageWidth(imagePath);
        var calculatedWidth = (columns * CONTAINER_WIDTH/GRID_COLUMNS) * RESOLUTION_MULTIPLIER;
        var imageWidthFromColumns = imageWidth;

        if (columns > 0 && imageWidth > calculatedWidth) {
            imageWidthFromColumns = calculatedWidth;
        }

        return imageWidthFromColumns;
    }

    /**
     * Checks image and returns resized or normal image file
     *
     * @param imagePath
     * @param imageWidthInColumns
     * @return image File object
     * @throws IOException
     */
    public static ImageResource resizeImage(String imagePath, int imageWidthInColumns) throws IOException, ResourceNotFoundException {
        var image = getImageResource(imagePath);
        var calculatedImageWidth = getImageWidthForColumns(imagePath, imageWidthInColumns);

        if (calculatedImageWidth < getImageWidth(imagePath)) {
            image = resizeImage(image, calculatedImageWidth);
        }

        return image;
    }

    public static ImageResource resizeImage(ImageResource image, int width) throws IOException {
        var imageFile = new File(image.getPath());
        // read image from file
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        var bWidth = (double) bufferedImage.getWidth();
        var bHeight = (double) bufferedImage.getHeight();
        var height = (int) Math.round(bHeight / bWidth * width);
        java.awt.Image tmp = bufferedImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
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
        var name = String.format(Files.getNameWithoutExtension(imageFile.getName()) + " - resized %d x %d", width, height);
        imageFile = new File(imageFile.getParentFile().getAbsolutePath() + File.separator + name + "." + extension);
        ImageIO.write(resizedImage, extension, imageFile);

        image.setPath(imageFile.getAbsolutePath());
        return image;
    }
}
