package ch.uzh.marugoto.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ch.uzh.marugoto.core.data.repository.TopicRepository;

@ShellComponent
public class ListAllTopicsCommand {

	@Autowired
	private TopicRepository topicRepository;
	
	@ShellMethod("List all topics")
	public void listTopics() {
		
		var topics = topicRepository.findAll().iterator();
		System.out.println("TOPICS:\n");
		while(topics.hasNext()){
			var topic = topics.next();
			System.out.println("title: " +topic.getTitle()+ "\n "+"id: "+topic.getId()+ "\r active: "+ topic.isActive()+"\n");
		}
	}
}
