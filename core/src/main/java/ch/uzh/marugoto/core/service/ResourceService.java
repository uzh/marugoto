package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.uzh.marugoto.core.data.entity.DocType;
import ch.uzh.marugoto.core.data.entity.DocumentResource;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.ImageType;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.entity.VideoResource;
import ch.uzh.marugoto.core.data.entity.VideoType;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.exception.ResourceNotFoundException;

@Service
public class ResourceService {

	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private FileService fileService;

	protected static Resource getFromPath(String resourcePath) throws ResourceNotFoundException {
		if (new File(resourcePath).exists()) {
			return new ImageResource(resourcePath);
		} else {
			throw new ResourceNotFoundException(resourcePath);
		}
	}

	public Resource getById(String id) {
		return resourceRepository.findById(id).orElse(null);
	}

	public void storeFile(MultipartFile file) throws IOException {
		String uploadDir = fileService.getUploadDir();
		try {
			byte[] bytes = file.getBytes();
			Path path = Paths.get(uploadDir + "/" + file.getOriginalFilename());
			Files.write(path, bytes);
			Resource resource = getResourceType(file);
			if (resource != null) {
				resourceRepository.save(resource);
			}
		}

		catch (IOException ex) {
			throw new RuntimeException("FAIL! -> message = " + ex.getMessage());
		}
	}
	
	public void deleteFile(String resourceId) throws IOException {
		Resource resource = getById(resourceId);
		Files.delete(Paths.get(resource.getPath()));
		resourceRepository.delete(resource);
	}
	
	public Resource getResourceType(MultipartFile file) {
		
		String fileName = file.getOriginalFilename();
		String filePath = fileService.getUploadDir()+ File.separator + fileName;
		
        String[] imageFormats = getNames(ImageType.class);
        String[] docFormats = getNames(DocType.class);
        String[] videoFormats = getNames(VideoType.class);
        
		if (stringContains(fileName, imageFormats)) {
			return new ImageResource(filePath);
		}
		else if (fileName.toUpperCase().contains(DocType.PDF.name())) {
			return new PdfResource(filePath);
		}
		else if (stringContains(fileName, docFormats)) {
			return new DocumentResource(filePath);
		}
		else if (stringContains(fileName, videoFormats)) {
			return new VideoResource(filePath);
		}
		return null;
	}
	private static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }
	
	public static String[] getNames(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	
}
