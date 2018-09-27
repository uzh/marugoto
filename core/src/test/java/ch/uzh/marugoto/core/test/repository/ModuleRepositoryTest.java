package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Module;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.ModuleRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class ModuleRepositoryTest extends BaseCoreTest{
	
	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private ModuleRepository moduleRepository;

	@Test
	public void testCreateModule() throws Exception {
		var page1 = pageRepository.save(new Page("Page 11", true, null));

		var testModule1 = moduleRepository.save(new Module("Module123", "icon-module-1", true, page1));
		assertNotNull(testModule1);
		assertEquals("Module123", testModule1.getTitle());
	}
}

