package ch.uzh.marugoto.shell.commands;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@ShellComponent
public class ListAllTopicsCommand {

	@Autowired
	private TopicRepository topicRepository;
	
	@ShellMethod("List all topics")
	public void listTopics() {
		
		List<Topic>topics = Lists.newArrayList(topicRepository.findAll().iterator());
		System.out.println("TOPICS:\n");
		topics.stream().forEach(topic -> {
			System.out.println("title: " +topic.getTitle()+ "\n "+"id: "+topic.getId()+ "\r active: "+ topic.isActive());
			System.out.println("\n");
		});
	}
}
