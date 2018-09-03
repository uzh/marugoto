package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Duration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.repository.StorylineRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class StorylineRepositoryTest extends BaseCoreTest{

	@Autowired
	private StorylineRepository storylineRepository;
	
	@Test
	public void testCreateStoryline() throws Exception {
		var testStoryline1 = storylineRepository.save(new Storyline("StorylineRepository","icon_storyline_repository",Duration.ofMinutes(10),true)); 
		assertNotNull(testStoryline1);
		assertEquals("StorylineRepository", testStoryline1.getTitle());
	}
}
