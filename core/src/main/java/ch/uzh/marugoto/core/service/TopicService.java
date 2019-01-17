package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@Service
public class TopicService {

	@Autowired
	private TopicRepository topicRepository;
	
	public List<Topic>listAll() {
		return Lists.newArrayList(topicRepository.findAll());
	}
	public Topic getTopic(String topicId) {
		return topicRepository.findById(topicId).orElseThrow();
	}
}
