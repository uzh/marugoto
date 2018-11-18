package ch.uzh.marugoto.shell.commands;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.arangodb.springframework.repository.ArangoRepository;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.repository.StorylineRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

@ShellComponent
public class DoImportCommand {

	private HashMap<Object, Object> map = new HashMap<Object, Object>();
	
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private StorylineRepository storylineRepository;
	@Autowired
	ObjectMapper mapper;
	
	
	@SuppressWarnings("unused")
	@ShellMethod("does insert or update of json file to database")
	public void doImportStep(String pathToDirectory, String insertMode) throws FileNotFoundException, IOException, ParseException, IllegalAccessException, InvocationTargetException, InstantiationException {
	
		if (insertMode.equals("insert")) {

			System.out.println(String.format("Insert data to db"));
			File[] files = getAllFiles(pathToDirectory);
			convertFromJsonToObject(files[0],Topic.class);
			for (int i = 0; i < files.length; i++) {
				if (!files[i].isDirectory()) {
					doImportForTopic(files[i],pathToDirectory);
				} 
				else {
					File[] storylineFiles = getAllFiles(pathToDirectory +"/"+ files[i].getName());
					System.out.println("dada");
					
				}			
			}
		}
		else if (insertMode.equals("update")) {
			//do update
			System.out.println(String.format("update data in db"));
			System.out.println(String.format(pathToDirectory));
		}
		else {
			// override
		}
	}
	
	private File[] getAllFiles(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		File[] files = folder.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return !file.isHidden();
		    }
		});
		return files;
	}
	
	private String readJsonFileFromDirectory(File file) throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		Object object = parser.parse(new FileReader(file.getAbsolutePath()));
		JSONObject json = (JSONObject) object;
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(json);
	}
	
	@SuppressWarnings({ "deprecation", "unchecked", "unused", "rawtypes" })
	private Object convertFromJsonToObject (File file, Class<?>cls) throws FileNotFoundException, IOException, ParseException, InstantiationException, IllegalAccessException {
	
		String topicJson = readJsonFileFromDirectory(file);
		//ObjectMapper mapper = new ObjectMapper();
		Object objectWithoutIds = cls.newInstance(); // new istance of the passed class
		objectWithoutIds = mapper.readValue(topicJson, cls); // cast json to java object
				
		mapperInitialazer ();
		ArangoRepository repo =  (ArangoRepository) map.get(cls);
		//objectWithIds = topic.save(objectWithoutIds);
		var res = repo.save(objectWithoutIds);
		
//		ArangoRepository objectWithIds =  (ArangoRepository) topicRepository.save((Topic)objectWithoutIds);
//		ArangoRepository repo = (ArangoRepository) map.get(cls);
//		objectWithIds = repo.save(objectWithoutIds); 
		
		return objectWithoutIds;
	}
	
	private void convertFromObjectToJson(Object obj, String pathToDirectory, String fileName) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(pathToDirectory + "/" + fileName + ".json"), obj);
	}
	
	private void doImportForTopic(File file, String pathToDirectory) throws FileNotFoundException, InstantiationException, IllegalAccessException, IOException, ParseException {
		Object topicObj  = convertFromJsonToObject(file, Topic.class);
		Topic topic = topicRepository.save((Topic) topicObj);
		convertFromObjectToJson(topic,pathToDirectory,"topic");
	}
	
//	private void doImportForStoryline(File file, String pathToDirectory, cl, repo) throws FileNotFoundException, InstantiationException, IllegalAccessException, IOException, ParseException {
//		Object storylineObj  = convertFromJsonToObject(file, Storyline.class);
//		Storyline storyline = storylineRepository.save((Storyline) storylineObj);
//		convertFromObjectToJson(storyline,pathToDirectory,"storyline");
//	}
	
	private void mapperInitialazer () {
	map.put(Topic.class, TopicRepository.class);
	map.put(Storyline.class, StorylineRepository.class);
}
	
	
//	private File[] getAllDirectories(String pathToDirectory) {
//		File folder = new File(pathToDirectory);
//		File[] files = folder.listFiles(new FileFilter() {
//		    @Override
//		    public boolean accept(File file) {
//		        return !file.isHidden() && file.isDirectory();
//		    }
//		});
//		return files;
//	}
	

//	private String execCmd(String cmd) throws java.io.IOException {
//	    @SuppressWarnings("resource")
//		Scanner scanner = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
//	    return scanner.hasNext() ? scanner.next() : "";
//	}
	
}
