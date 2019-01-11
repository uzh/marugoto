package ch.uzh.marugoto.core.service;

import ch.uzh.marugoto.core.data.entity.AudioResource;
import ch.uzh.marugoto.core.data.entity.AudioType;
import ch.uzh.marugoto.core.data.entity.DocType;
import ch.uzh.marugoto.core.data.entity.DocumentResource;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.ImageType;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.entity.VideoResource;
import ch.uzh.marugoto.core.data.entity.VideoType;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.helpers.StringHelper;

public abstract class ResourceFactory {

    public static String getResourceType(String fileName) throws ResourceTypeResolveException {
        String type;

        if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(ImageType.class))) {
            type = "image";
        } else if (fileName.toUpperCase().equals(DocType.PDF.name())) {
            type = "pdf";
        } else if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(DocType.class))) {
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

    public static Resource getResource(String fileName) throws ResourceTypeResolveException {
        switch (getResourceType(fileName)) {
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
}
