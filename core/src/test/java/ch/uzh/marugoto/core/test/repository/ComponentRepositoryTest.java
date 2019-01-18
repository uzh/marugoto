package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.TextComponent;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertNotNull;

/**
 * Simple test cases for TextComponent entities.
 */
public class ComponentRepositoryTest extends BaseCoreTest {

	@Autowired
	private ComponentRepository componentRepository;

	@Test
	public void test1CreateTextComponent() {
		var textComponent1 = componentRepository.save(new TextComponent(6, "Text component create test"));
		assertNotNull(textComponent1);
	}
}
