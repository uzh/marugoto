package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.data.entity.DocumentResource;
import ch.uzh.marugoto.core.data.entity.DocumentType;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.ImageType;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.entity.VideoResource;
import ch.uzh.marugoto.core.data.entity.VideoType;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;
import ch.uzh.marugoto.core.helpers.StringHelper;

@Service
public class ResourceService {
	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	protected FileService fileService;
	@Value("${resource.dir}")
	protected String resourceDirectory;

	public File getResourceFile(String resourceFileName) throws ResourceTypeResolveException {
		var subfolder = ResourceFactory.getResourceType(resourceFileName);
		var resourcePath = resourceDirectory + File.separator + subfolder + File.separator + resourceFileName;
		return new File(resourcePath);
	}

	/**
	 * Copies file to static resource folder accessible by URL
	 *
	 * @param filePath
	 * @return relative file path
	 */
	protected String copyFileToResourceFolder(Path filePath) {
		try {
			var fileName = filePath.getFileName().toString();
			var subfolder = ResourceFactory.getResourceType(fileName);
			var destination = resourceDirectory + File.separator + subfolder;
			 FileService.copyFile(filePath, Paths.get(destination));
			return subfolder + File.separator + fileName;
		} catch (ResourceTypeResolveException | IOException e) {
			throw new RuntimeException("Error copying to resource folder: " + e.getMessage() + ". " + e.getCause());
		}
	}

	public Resource getById(String id) {
		return resourceRepository.findById(id).orElse(null);
	}

	/**
	 * Saves resource to database
	 *
	 * @param resource
	 * @return
	 */
	public Resource saveResource(Resource resource) {
		return resourceRepository.save(resource);
	}

	public String uploadResource(MultipartFile file) throws ResourceTypeResolveException {
		Resource resource = ResourceFactory.getResource(file.getOriginalFilename());
		String filePath = fileService.uploadFile(file);
		resource.setPath(filePath);
		saveResource(resource);
		return filePath;
	}
	
	public Resource renameResource(String filePath, String newResourceName) {
		Resource resource = resourceRepository.findByPath(filePath);
    	newResourceName = newResourceName.replaceAll("[^0-9]","");
    	resource.setPath(fileService.renameFile(Paths.get(filePath), newResourceName));
    	return saveResource(resource);
	}
	
	public void deleteFile(String resourceId) throws IOException {
		Resource resource = getById(resourceId);
		Path path = Paths.get(resource.getPath());
		if (path.toFile().exists()) {
			Files.delete(path);	
		}
		resourceRepository.delete(resource);
	}
	
	public Resource getResourceType(String fileName) {

		if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(ImageType.class))) {
			return new ImageResource();
		}
		else if (fileName.toUpperCase().contains(DocumentType.PDF.name())) {
			return new PdfResource();
		}
		else if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(DocumentType.class))) {
			return new DocumentResource();
		}
		else if (StringHelper.stringContains(fileName.toUpperCase(), StringHelper.getEnumValues(VideoType.class))) {
			return new VideoResource();
		}
		else {
			return null;			
		}
	}
}
