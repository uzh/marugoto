package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.StorylineStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class StorylineStateRepositoryTest extends BaseCoreTest{

	@Autowired
	private UserRepository userRepository; 
	
	@Autowired
	private StorylineStateRepository storylineStateRepository; 
	
	@Test
	public void testCreateStorylineState () {
		var user = userRepository.save(new User(UserType.Guest, Salutation.Mr, "Page", "State", "storyline.state@test.com", "test"));
		var started  = LocalDateTime.now();
		var storylineState = storylineStateRepository.save(new StorylineState(started,user));
		
		assertNotNull(storylineState);
		assertEquals(user.getFirstName(), storylineState.getUser().getFirstName());
	}
}