package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class TopicRepositoryTest extends BaseCoreTest{
	
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private TopicRepository topicRepository;

	@Test
	public void testCreateTopic() {
		var page1 = pageRepository.save(new Page("Page 11", true, null));

		var testTopic1 = topicRepository.save(new Topic("Topic123", "icon-topic-1", true, page1));
		assertNotNull(testTopic1);
		assertEquals("Topic123", testTopic1.getTitle());
	}
}

