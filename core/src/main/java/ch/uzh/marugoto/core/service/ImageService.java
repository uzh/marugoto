package ch.uzh.marugoto.core.service;

import com.google.common.io.Files;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageService {

    private static final int GRID_COLUMNS = 12;
    private static final int CONTAINER_WIDTH = 1600;

    /**
     * Checks image and returns resized or normal image file
     *
     * @param imageUrl
     * @param imageWidthInColumns
     * @return image File object
     * @throws IOException
     */
    public static File getImage(String imageUrl, int imageWidthInColumns) throws IOException {
        var image = new File(imageUrl);

        if (imageWidthInColumns > 0) {
            var imageWidth = ImageIO.read(image).getWidth();
            var calcWidth = (CONTAINER_WIDTH/GRID_COLUMNS) * imageWidthInColumns;

            if (imageWidth > calcWidth) {
                image = resize(imageUrl, calcWidth);
            }
        }

        return image;
    }

    private static File resize(String imageUrl, int width) throws IOException {
        var imageFile = new File(imageUrl);
        // read image from file
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        var bWidth = (double) bufferedImage.getWidth();
        var bHeight = (double) bufferedImage.getHeight();
        var height = (int) Math.round(bHeight / bWidth * width);
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
        var name = String.format(Files.getNameWithoutExtension(imageFile.getName()) + " - resized %d x %d", width, height);
        imageFile = new File(imageFile.getParentFile().getAbsolutePath() + File.separator + name + "." + extension);
        ImageIO.write(resizedImage, extension, imageFile);
        return imageFile;
    }
}
