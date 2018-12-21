package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

	public String storeFile(MultipartFile file) throws IOException {
		String filePath = null;
		String uploadDir = fileService.getUploadDir();
		try {
			Path rootLocation = Paths.get(uploadDir); 
			Files.copy(file.getInputStream(),rootLocation.resolve(file.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);
			Resource resource = getResourceType(file.getOriginalFilename());
			if (resource != null) {
				filePath = uploadDir + File.separator + file.getOriginalFilename();
				resource.setPath(filePath);
				resourceRepository.save(resource);
			}
		}
		catch (IOException ex) {
			throw new RuntimeException("Error: " + ex.getMessage());
		}
		return filePath;
	}
	
	public void deleteFile(String resourceId) throws IOException {
		Resource resource = getById(resourceId);
		Files.delete(Paths.get(resource.getPath()));
		resourceRepository.delete(resource);
	}
	
	public Resource getResourceType(String fileName) {
		

		if (stringContains(fileName.toUpperCase(), getNames(ImageType.class))) {
			return new ImageResource();
		}
		else if (fileName.toUpperCase().contains(DocType.PDF.name())) {
			return new PdfResource();
		}
		else if (stringContains(fileName.toUpperCase(), getNames(DocType.class))) {
			return new DocumentResource();
		}
		else if (stringContains(fileName.toUpperCase(), getNames(VideoType.class))) {
			return new VideoResource();
		}
		else {
			return null;			
		}
	}
	private static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }
	
	public static String[] getNames(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	
}