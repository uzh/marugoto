package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertNotNull;

import java.time.Duration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.entity.state.TopicState;
import ch.uzh.marugoto.core.data.repository.TopicStateRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class StorylineStateRepositoryTest extends BaseCoreTest{

	@Autowired
	private TopicStateRepository topicStateRepository;
	
	@Test
	public void testCreateStorylineState () {
		var topic = new Topic();
		var topicState = topicStateRepository.save(new TopicState(topic));
		
		assertNotNull(topicState);
	}
}