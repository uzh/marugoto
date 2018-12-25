package ch.uzh.marugoto.core.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FilenameUtils;
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
		fileService.storeFile(file);
		Resource resource = getResourceType(file.getOriginalFilename());
		if (resource != null) {
			filePath = uploadDir + File.separator + file.getOriginalFilename();
			resource.setPath(filePath);
			resourceRepository.save(resource);
		}
		return filePath;	
	}
	
	public Resource renameResource(String absolutPathName, String newResourceName) throws Exception {
		Resource resource = resourceRepository.findByPath(absolutPathName);
    	String fileExtension = FilenameUtils.getExtension(absolutPathName);
    	newResourceName = newResourceName.replaceAll("[^0-9]","");
		
		String newFileName = newResourceName+ "." + fileExtension;
    	String newFilePath = fileService.renameFile(absolutPathName, newFileName);
    	resource.setPath(newFilePath);
    	return resourceRepository.save(resource);
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

		if (FileService.stringContains(fileName.toUpperCase(), FileService.getEnumValues(ImageType.class))) {
			return new ImageResource();
		}
		else if (fileName.toUpperCase().contains(DocType.PDF.name())) {
			return new PdfResource();
		}
		else if (FileService.stringContains(fileName.toUpperCase(), FileService.getEnumValues(DocType.class))) {
			return new DocumentResource();
		}
		else if (FileService.stringContains(fileName.toUpperCase(), FileService.getEnumValues(VideoType.class))) {
			return new VideoResource();
		}
		else {
			return null;			
		}
	}
}
