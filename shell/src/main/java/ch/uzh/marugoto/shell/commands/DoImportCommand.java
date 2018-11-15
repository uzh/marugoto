package ch.uzh.marugoto.shell.commands;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@ShellComponent
public class DoImportCommand {

	//private HashMap<String, String> map = { Storyline.class, StorylineRepository.class};
	
	@Autowired
	private TopicRepository topicRepository;
	
	@ShellMethod("does insert or update of json file to database")
	public void doImportStep(String pathToDirectory, String insertMode) throws FileNotFoundException, IOException, ParseException, IllegalAccessException, InvocationTargetException {
	
		if (insertMode.equals("insert")) {

			System.out.println(String.format("Insert data to db"));
			File[] files = filterHiddenFiles(pathToDirectory);
			
//			Process p = Runtime.getRuntime().exec(new String[]{"sh","-c","which bash"});
			var res = execCmd("arangoexport --server.database dev --collection page --output-directory “dusan” --overwrite true");
			
			for (int i = 0; i < files.length; i++) {
				String json = readJsonFileFromDirectory(files[i]);
				
				ObjectMapper mapper = new ObjectMapper();
				Topic topicWithoutIds = mapper.readValue(json, Topic.class);
				Topic topic = topicRepository.save(topicWithoutIds);
			
				//System.out.println("topicid: " + topic.getId());		
			}
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
	
//	private Object convertJsonToPOJO(String json, Object o) {
//		ObjectMapper mapper = new ObjectMapper();
//		
//		Topic topicWithoutIds = mapper.readValue(json, Topic.class);
//		//Topic topic = topicRepository.save(topicWithoutIds);
//		return topic;
//		
//	}
	
	private File[] filterHiddenFiles(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		File[] files = folder.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return !file.isHidden();
		    }
		});
		return files;
	}
	
	private String execCmd(String cmd) throws java.io.IOException {
	    Scanner s = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	private String readJsonFileFromDirectory(File file) throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		Object object = parser.parse(new FileReader(file.getAbsolutePath()));
		JSONObject json = (JSONObject) object;
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(json);
	}
	
}
