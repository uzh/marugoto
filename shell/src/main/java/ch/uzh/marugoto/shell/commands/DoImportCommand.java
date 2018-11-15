package ch.uzh.marugoto.shell.commands;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.parser.ParseException;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.core.data.entity.Topic;

@ShellComponent
public class DoImportCommand {

	
//	@Autowired
//	private TopicRepository topicRepository;
//	
	@ShellMethod("does insert or update of json file to database")
	public void doImportStep(String pathToDirectory, String insertMode) throws FileNotFoundException, IOException, ParseException, IllegalAccessException, InvocationTargetException {
	
		if (insertMode.equals("insert")) {
			System.out.println(String.format("Insert data to db"));

			//pathToDirectory = "/Users/tomic/Desktop/importData"				
//			 JSONParser parser = new JSONParser();
//			 Object object = parser.parse(new FileReader(pathToDirectory + "/topic.json"));
//			 JSONArray json = (JSONArray) object;
			 ObjectMapper mapper = new ObjectMapper();
			 Topic topic = mapper.readValue(new FileReader(pathToDirectory + "/topic.json"), Topic.class);
//			 //User user = mapper.readValue(new File("c:\\user.json"), User.class);

		     System.out.println(topic);		
			
		}
		else if (insertMode.equals("update")) {
			//do update
			System.out.println(String.format("update data in db"));
			System.out.println(String.format(pathToDirectory));
		}
		else {
			//probably override
		}
		
	}
	
}
