package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.service.TopicService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class TopicServiceTest extends BaseCoreTest{

	@Autowired
	private TopicService topicService;
	@Autowired 
	private PageRepository pageRepository;
	@Autowired
	private TopicRepository topicRepository;
	private Page page;
	private Topic topic1;
	private Topic topic2;
	private Topic topic3;
	
	@Before
	public void init() {
		page = pageRepository.findByTitle("Page 1");
		topic1 = new Topic("Topic1", "icon-topic-1", true,page);
		topic2 = new Topic("Topic2", "icon-topic-2", true,page);
		topic3 = new Topic("Topic3", "icon-topic-3", true,page);
		topicRepository.save(topic1);
		topicRepository.save(topic2);
		topicRepository.save(topic3);
	}
	
	@Test
	public void testFindAllActivatedTopics() {
		
		topic1.setActive(false);
		topicRepository.save(topic1);
		var topics = topicService.listAll();
		assertNotNull(topics);
		assertThat(topics.size(),is(3));
		
		topic1.setActive(true);
		topicRepository.save(topic1);
		topics = topicService.listAll();
		assertThat(topics.size(),is(4));
	}
	
	@Test
	public void testGetTopic() {
		Topic topic = topicService.getTopic(topic1.getId());
		assertNotNull(topic);
	}

	@Test
	public void testGetTopicStartPage() {
		Page page = topicService.getTopicStartPage(topic1.getId());
		assertNotNull(page);
	}
}