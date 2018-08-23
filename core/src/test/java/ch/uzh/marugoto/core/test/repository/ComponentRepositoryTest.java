package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.TextComponent;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple test cases for TextComponent entities.
 */
public class ComponentRepositoryTest extends BaseCoreTest {

	@Autowired
	private ComponentRepository componentRepository;

	@Test
	public void test1CreateTextComponent() throws Exception {
		var textComponent1 = componentRepository.save(new TextComponent(0, 100, 150, 150, "Title", "Text component create test"));
		assertNotNull(textComponent1);
	}
}
