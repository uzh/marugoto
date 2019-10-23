package ch.uzh.marugoto.core.test.service;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.resource.ComponentResource;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.service.ComponentService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Simple tests for the ComponentService class
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComponentServiceTest extends BaseCoreTest {

	@Autowired
	private ComponentService componentService;
	@Autowired
	private PageRepository pageRepository;
	
	@Override
	protected void setupOnce() {
		super.setupOnce();
	}

	@Test
	public void testGetPageComponents() {
		var page = pageRepository.findByTitle("Page 1");
		var components = componentService.getPageComponents(page);
		assertFalse(components.isEmpty());
	}

	@Test
	public void testGetComponentResources() {
		var page = pageRepository.findByTitle("Page 1");
		var componentsResources = componentService.getComponentResources(page);
		assertThat(componentsResources.get(0), instanceOf(ComponentResource.class));
	}
}
