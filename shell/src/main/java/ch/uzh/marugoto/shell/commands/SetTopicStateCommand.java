package ch.uzh.marugoto.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@ShellComponent
public class SetTopicStateCommand {

	@Autowired
	private TopicRepository topicRepository;
	enum ACTIVATE {TRUE, FALSE}
	
	@ShellMethod("`true/false topicId` Used for activating or deactivating certain topic.")
	public void setTopicState(String active, String topicId) throws Exception {

		Topic topic = topicRepository.findById(topicId).orElseThrow();
	
		if (active.toUpperCase().equals(ACTIVATE.TRUE.toString())) {
			topic.setActive(true);
			System.out.println("Topic is successfully activated");
			
		} else if (active.toUpperCase().equals(ACTIVATE.FALSE.toString())){
			topic.setActive(false);
			System.out.println("Topic is successfully deactivated");
		}
		else {
			throw new Exception("Something went wrong! Please check your input");
		}
		topicRepository.save(topic);
	}
}
