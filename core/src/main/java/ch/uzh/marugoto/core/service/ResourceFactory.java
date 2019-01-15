package ch.uzh.marugoto.core.service;

import ch.uzh.marugoto.core.Constants;
import ch.uzh.marugoto.core.data.entity.AudioResource;
import ch.uzh.marugoto.core.data.entity.AudioType;
import ch.uzh.marugoto.core.data.entity.DocumentResource;
import ch.uzh.marugoto.core.data.entity.DocumentType;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.ImageType;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.entity.VideoResource;
import ch.uzh.marugoto.core.data.entity.VideoType;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.helpers.StringHelper;

public abstract class ResourceFactory {

    public static String[] getResourceTypes() {
        return Constants.RESOURCE_TYPES;
    }

    /**
     * Get resource instance
     * first tries to find it by name and after
     * by guessed resource type
     *
     * @param fileName
     * @return
     * @throws ResourceTypeResolveException
     */
    public static Resource getResource(String fileName) throws ResourceTypeResolveException {
        Resource resource;
        try {
            resource = getInstance(fileName);
        } catch (ResourceTypeResolveException e) {
            resource = getInstance(getResourceType(fileName));
        }
        return resource;
    }

    /**
     * Returns resource instance by name
     *
     * @param name
     * @return
     * @throws ResourceTypeResolveException
     */
    private static Resource getInstance(String name) throws ResourceTypeResolveException {
        switch (name) {
            case "image":
                return new ImageResource();
            case "pdf":
                return new PdfResource();
            case "document":
                return new DocumentResource();
            case "audio":
                return new AudioResource();
            case "video":
                return new VideoResource();
            default:
                throw new ResourceTypeResolveException();
        }
    }

    /**
     * Guess resource type from file name
     *
     * @param fileName
     * @return
     * @throws ResourceTypeResolveException
     */
    public static String getResourceType(String fileName) throws ResourceTypeResolveException {
        String type;

        if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(ImageType.class))) {
            type = "image";
        } else if (fileName.toUpperCase().equals(DocumentType.PDF.name())) {
            type = "pdf";
        } else if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(DocumentType.class))) {
            type = "document";
        } else if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(AudioType.class))) {
            type = "audio";
        } else if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(VideoType.class))) {
            type = "video";
        } else {
            throw new ResourceTypeResolveException();
        }

        return type;
    }
}
