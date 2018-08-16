/**
 * 
 */
package ch.uzh.marugoto.backend.test.data;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.marugoto.backend.data.entity.TextComponent;
import ch.uzh.marugoto.backend.data.repository.ComponentRepository;
import ch.uzh.marugoto.backend.test.BaseTest;

/**
 * Simple test cases for TextComponent entities.
 * 
 * @author nemtish
 */
public class ComponentEntitiesTest extends BaseTest {

	@Autowired
	private ComponentRepository componentRepository;

	@Test
	public void test1CreateTextComponent() throws Exception {
		var txtCmp1 = componentRepository.save(new TextComponent(0, 100, 150, 150, "Text component create test"));
		assertNotNull(txtCmp1);
	}
}
