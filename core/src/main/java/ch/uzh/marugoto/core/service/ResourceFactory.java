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

public abstract class ResourceFactory {

    public static String getResourceType(String fileName) throws ResourceTypeResolveException {
        String type;

        if (FileService.stringContains(fileName.toUpperCase(), FileService.getEnumValues(ImageType.class))) {
            type = "image";
        } else if (fileName.toUpperCase().equals(DocType.PDF.name())) {
            type = "pdf";
        } else if (FileService.stringContains(fileName.toUpperCase(), FileService.getEnumValues(DocType.class))) {
            type = "document";
        } else if (FileService.stringContains(fileName.toUpperCase(), FileService.getEnumValues(AudioType.class))) {
            type = "audio";
        } else if (FileService.stringContains(fileName.toUpperCase(), FileService.getEnumValues(VideoType.class))) {
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
