package ch.uzh.marugoto.core.service;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;

@Service
public class ImageService extends ResourceService {
    /**
     * Returns image width
     *
     * @param imagePath
     * @return
     * @throws IOException
     */
    public int getImageWidth(Path imagePath) throws IOException {
        return ImageIO.read(imagePath.toFile()).getWidth();
    }

    /**
     * Save image resource to database
     *
     * @param imagePath
     * @return
     * @throws ResourceNotFoundException
     */
    public ImageResource saveImageResource(Path imagePath, int numberOfColumns) throws ResourceNotFoundException, IOException {
        if (imagePath.toFile().exists() == false) {
            throw new ResourceNotFoundException(imagePath.toFile().getAbsolutePath());
        }

        ImageResource imageResource = new ImageResource();
        imageResource.setPath(copyFileToResourceFolder(resizeImage(imagePath, getImageWidthFromColumns(numberOfColumns))));
        imageResource.setThumbnailPath(copyFileToResourceFolder(resizeImage(imagePath, Constants.THUMBNAIL_WIDTH)));
        saveResource(imageResource);

        return imageResource;
    }

    /**
     * Returns image width from number of grid columns
     *
     * @param columns
     * @return
     */
    public int getImageWidthFromColumns(int columns) {
        int imageWidthFromColumns;

        switch (columns) {
            case 10:
                imageWidthFromColumns = Constants.IMAGE_WIDTH_COL_10;
                break;
            case 6:
                imageWidthFromColumns = Constants.IMAGE_WIDTH_COL_6;
                break;
            case 5:
                imageWidthFromColumns = Constants.IMAGE_WIDTH_COL_5;
                break;
            case 4:
                imageWidthFromColumns = Constants.IMAGE_WIDTH_COL_4;
                break;
            case 3:
                imageWidthFromColumns = Constants.IMAGE_WIDTH_COL_3;
                break;
            case 1:
                imageWidthFromColumns = Constants.IMAGE_WIDTH_COL_1;
                break;
            default:
                imageWidthFromColumns = Constants.IMAGE_WIDTH_COL_12;
        }

        return imageWidthFromColumns;
    }

    /**
     * Resizing image to provided width
     *
     * @param imagePath
     * @param width
     * @return
     * @throws IOException
     */
    public Path resizeImage(Path imagePath, int width) throws IOException {
        // disallow upscaling
        if (getImageWidth(imagePath) <= width) {
            return imagePath;
        }

        var imageFile = imagePath.toFile();
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
        var extension = FilenameUtils.getExtension(imageFile.getName());
        var name = String.format(FilenameUtils.getBaseName(imagePath.toFile().getName()) + " (%d x %d)", width, resizedImage.getHeight());
        imageFile = new File(imagePath.toFile().getParentFile().getAbsolutePath() + File.separator + name + "." + extension);
        ImageIO.write(resizedImage, extension, imageFile);

        return Paths.get(imageFile.getAbsolutePath());
    }
}
