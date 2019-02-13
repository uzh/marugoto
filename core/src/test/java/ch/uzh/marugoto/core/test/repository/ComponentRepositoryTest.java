package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.topic.TextComponent;
import ch.uzh.marugoto.core.data.entity.topic.TextExercise;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Simple test cases for TextComponent entities.
 */
public class ComponentRepositoryTest extends BaseCoreTest {

	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private PageRepository pageRepository;

	@Test
	public void test1CreateTextComponent() {
		var textComponent1 = componentRepository.save(new TextComponent(6, "Text component create test"));
		assertNotNull(textComponent1);
	}

	@Test
	public void testFindByPageIdOrderByRenderOrderAsc() {
		var page = pageRepository.findByTitle("Page 5");
		var cmp = componentRepository.findPageComponents(page.getId());
		assertThat(cmp.isEmpty(), is(true));

		page = pageRepository.findByTitle("Page 1");
		cmp = componentRepository.findPageComponents(page.getId());
		assertThat(cmp.size(), is(2));
		assertThat(cmp.get(1), instanceOf(TextExercise.class));
	}
}
