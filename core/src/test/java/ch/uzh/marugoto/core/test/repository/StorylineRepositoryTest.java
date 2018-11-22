package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.repository.StorylineRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StorylineRepositoryTest extends BaseCoreTest{

	@Autowired
	private StorylineRepository storylineRepository;
	
	@Test
	public void testCreateStoryline() {
		var testStoryline1 = storylineRepository.save(new Storyline("StorylineRepository", "icon_storyline_repository", Duration.ofMinutes(10))); 
		assertNotNull(testStoryline1);
		assertEquals("StorylineRepository", testStoryline1.getTitle());
	}
}
