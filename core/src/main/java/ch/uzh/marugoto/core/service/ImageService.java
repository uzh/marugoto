package ch.uzh.marugoto.core.service;

import com.google.common.io.Files;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;

public class ImageService {

    private static final int GRID_COLUMNS = 12;
    private static final int CONTAINER_WIDTH = 1600;
    private static final int RESOLUTION_MULTIPLIER = 2;

    /**
     * Checks image and returns resized or normal image file
     *
     * @param imageUrl
     * @param imageWidthInColumns
     * @return image File object
     * @throws IOException
     */
    public static ImageResource getImage(String imageUrl, int imageWidthInColumns) throws IOException, ResourceNotFoundException {
        var imageFile = new File(imageUrl);

        if (!imageFile.exists()) {
            throw new ResourceNotFoundException(imageUrl);
        }

        var image = new ImageResource(imageUrl);

        if (imageWidthInColumns > 0) {
            var imageWidth = ImageIO.read(imageFile).getWidth();
            var calcWidth = (imageWidthInColumns * CONTAINER_WIDTH/GRID_COLUMNS) * RESOLUTION_MULTIPLIER;

            if (imageWidth > calcWidth) {
                var resizedImage = resize(image, calcWidth);
                image.setPath(resizedImage.getPath());
            }
        }

        return image;
    }

    private static ImageResource resize(ImageResource image, int width) throws IOException {
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
