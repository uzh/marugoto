/**
 * 
 */
package ch.uzh.marugoto.backend.test.data;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.TextComponent;
import ch.uzh.marugoto.backend.data.repository.ComponentRepository;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.test.BaseTest;

/**
 * Simple test cases for TextComponent entities.
 * 
 */
public class ComponentEntitiesTest extends BaseTest {

	@Autowired
	private ComponentRepository componentRepository;
	
	@Autowired
	private PageRepository pageRepository;

	@Test
	public void test1CreateTextComponent() throws Exception {

		if (false == pageRepository.findAll().iterator().hasNext()) {
			pageRepository.save(new Page("Page 1", true, null));
		}
		
		var pages = Lists.newArrayList(pageRepository.findAll());
		var txtCmp1 = componentRepository.save(new TextComponent(0, 100, 150, 150, pages.get(0), "Text component create test"));
		
		assertNotNull(txtCmp1);
	}
}
