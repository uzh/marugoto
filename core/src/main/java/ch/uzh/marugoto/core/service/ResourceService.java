package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;

@Service
public class ResourceService {

	@Autowired
	private ResourceRepository resourceRepository;

	@Autowired
	protected FileService fileService;

	@Value("${resource.dir}")
	protected String resourceDirectory;

	/**
	 * Copies file to static resource folder accessible by URL
	 *
	 * @param filePath
	 * @return relative file path
	 */
	public String copyFileToResourceFolder(Path filePath) {
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
		var copiedPath = copyFileToResourceFolder(Paths.get(resource.getPath()));
		resource.setPath(copiedPath);
		return resourceRepository.save(resource);
	}
}
