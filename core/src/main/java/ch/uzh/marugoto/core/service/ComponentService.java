 package ch.uzh.marugoto.core.service;

 import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.topic.Component;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.resource.ComponentResource;

 /**
 * 
 * Base Service for all components
 *
 */
@Service
public class ComponentService {

	@Autowired
	private ComponentRepository componentRepository;

	/**
	 * Find specific component by ID
	 *
	 * @param componentId
	 * @return
	 */
	public Component findById(String componentId) {
		return componentRepository.findById(componentId).orElseThrow();
	}

	/**
	 * Returns all components that belongs to the page
	 *
	 * @param page
	 * @return components
	 */
	public List<Component> getPageComponents(Page page) {
		return componentRepository.findPageComponents(page.getId());
	}

	 /**
	  * Returns all component resources (components with states) for the page
	  *
	  * @param page Page
	  * @return components
	  */
	public List<ComponentResource> getComponentResources(Page page) {
		List<ComponentResource> resourceList = new ArrayList<>();

		for (Component component : getPageComponents(page)) {
			resourceList.add(new ComponentResource(component));
		}

		return resourceList;
	}
}
