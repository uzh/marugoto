package ch.uzh.marugoto.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@ShellComponent
public class ActivateTopicCommand {

	@Autowired
	private TopicRepository topicRepository;
	enum ACTIVATE {TRUE, FALSE}
	
	@SuppressWarnings("unlikely-arg-type")
	@ShellMethod("`true/false topicId` Used for activating or deactivating certain topic.")
	public void activateTopic(String active, String topicId) throws Exception {

		Topic topic = topicRepository.findById(topicId).orElseThrow();
		
		if (active.equals(ACTIVATE.TRUE)) {
			topic.setActive(true);
			System.out.println("Topic is successfully activated");
			
		} else if (active.equals(ACTIVATE.FALSE)){
			topic.setActive(false);
			System.out.println("Topic is successfully deactivated");
		}
		else {
			throw new Exception("Something went wrong! Please check your input");
		}
		topicRepository.save(topic);
	}
}
