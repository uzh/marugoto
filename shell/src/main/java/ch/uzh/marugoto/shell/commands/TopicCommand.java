package ch.uzh.marugoto.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@ShellComponent
public class TopicCommand {

	@Autowired
	private TopicRepository topicRepository;
	
	@ShellMethod("List all topics")
	public void listTopics() {
		
		var topics = topicRepository.findAll().iterator();
		System.out.println("TOPICS:\n");
		while(topics.hasNext()){
			var topic = topics.next();
			System.out.println("title:" +topic.getTitle()+ " "+"id:"+topic.getId()+ " active:"+ topic.isActive()+"\r\n");
		}
	}

	@ShellMethod("`true/false topicId` Used for activating or deactivating certain topic.")
	public void setTopicState(String active, String topicId) throws Exception {

		Topic topic = topicRepository.findById(topicId).orElseThrow();

		if (active.toLowerCase().equals(Boolean.TRUE.toString())) {
			topic.setActive(true);
			System.out.println("Topic is successfully activated");

		} else if (active.toLowerCase().equals(Boolean.FALSE.toString())){
			topic.setActive(false);
			System.out.println("Topic is successfully deactivated");
		}
		else {
			throw new Exception("Something went wrong! Please check your input");
		}
		topicRepository.save(topic);
	}
}
