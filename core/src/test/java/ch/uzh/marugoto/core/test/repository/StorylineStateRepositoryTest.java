package ch.uzh.marugoto.core.test.repository;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.repository.StorylineStateRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import static org.junit.Assert.assertNotNull;

public class StorylineStateRepositoryTest extends BaseCoreTest{

	@Autowired
	private StorylineStateRepository storylineStateRepository; 
	
	@Test
	public void testCreateStorylineState () {
		var testStoryline1 = new Storyline("Storyline-1", "icon-storyline-1", Duration.ofMinutes(10));
		var storylineState = storylineStateRepository.save(new StorylineState(testStoryline1));
		
		assertNotNull(storylineState);
	}
}