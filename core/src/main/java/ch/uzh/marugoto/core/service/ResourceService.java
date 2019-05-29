package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.Resource;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.exception.ResourceTypeResolveException;

@Service
public class ResourceService {

	@Value("${marugoto.resource.static.dir}")
	private String resourceDirectory;
	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	protected FileService fileService;

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

	public Resource checkIfResourceExist(Resource resource) {
		try {
//			if (resource.getId() == null) {
				var resourcePath = Paths.get(resource.getPath());
				var fileName = resourcePath.getFileName().toString();
				var subfolder = ResourceFactory.getResourceType(fileName);
				var resourceFromDb = resourceRepository.findByPath(subfolder + File.separator + fileName);
				if (resourceFromDb != null) {
					resource = resourceFromDb;
				}
//			}
		} catch (ResourceTypeResolveException e) {
			System.out.format("Resource exists check error: %s", e.getMessage());
		}

		return resource;
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
		resource = checkIfResourceExist(resource);

		if (resource.getId() == null) {
			var resourcePath = copyFileToResourceFolder(Paths.get(resource.getPath()));
			resource.setPath(resourcePath);

			if (resource instanceof ImageResource) {
				ImageResource imageResource = (ImageResource) resource;
				var thummbnailPath = copyFileToResourceFolder(Paths.get(imageResource.getThumbnailPath()));
				imageResource.setThumbnailPath(thummbnailPath);
			}

			resource = resourceRepository.save(resource);
		}

		return resource;
	}
}
