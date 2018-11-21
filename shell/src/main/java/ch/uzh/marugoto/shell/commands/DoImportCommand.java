package ch.uzh.marugoto.shell.commands;

import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.repository.ArangoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;

@ShellComponent
public class DoImportCommand {

	private final HashMap<String, Object>SAVED_OBJECTS = new HashMap<String, Object>();
	@Autowired
	private ObjectMapper mapper;
	@Autowired
    private ApplicationContext appContext;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private ArangoOperations operations;
	@Autowired
	private DbConfiguration dbConfig;
	private Object obj;
	
	
	@ShellMethod("does insert or update of json file to database")
	public void doImportStep(String pathToDirectory, String insertMode) throws IOException, ParseException, IllegalAccessException, InstantiationException, ClassNotFoundException {
	
		if (insertMode.equals("insert")) {
			System.out.println(String.format("Truncating database `%s`...", dbConfig.database()));
			operations.dropDatabase();
			operations.driver().createDatabase(dbConfig.database());
			System.out.println(String.format("Insert data to db"));
			doImport(pathToDirectory);
			System.out.println(String.format("collections are inserted"));
			setRelations();
			System.out.println(String.format("Added relations between collections"));
		}
		else if (insertMode.equals("update")) {
			//do update
			System.out.println(String.format("update data in db"));
		}
		else {
			// override
		}
	}
	
	private File[] getAllFiles(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		File[] files = folder.listFiles(file -> !file.isHidden());
		return files;
	}
	
	private File[] getAllDirectories(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		File[] files = folder.listFiles(file -> !file.isHidden() && file.isDirectory());
		return files;
	}
	
	private String readJsonFileFromDirectory(File file) throws IOException, ParseException {

		JSONParser parser = new JSONParser();
		Object object = parser.parse(new FileReader(file.getAbsolutePath()));
		JSONObject json = (JSONObject) object;
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(json);
	}
	
	@SuppressWarnings({ "all" })
	private Object convertFromJsonToObject (File file, Class<?>cls) throws FileNotFoundException, IOException, ParseException, InstantiationException, IllegalAccessException {
	
		String json = readJsonFileFromDirectory(file);
		Object objectWithoutIds = cls.newInstance(); // new istance of the passed class
		objectWithoutIds = mapper.readValue(json, cls); // cast json to java object
		
		Object objectWithIds  = null;
		ArangoRepository repository = null;
		String className = objectWithoutIds.getClass().getName();
		String[] items = new String[] {"Exercise", "Component"};
		
		if (stringContains(className,items)) {
			repository = componentRepository;
		} else {
			repository = getRepositoryName(objectWithoutIds);	
		}
		objectWithIds = repository.save(objectWithoutIds);
		return objectWithIds;
	}
	
	private void convertFromObjectToJson(Object obj, String pathToDirectory, String fileName) throws IOException {
		mapper.writeValue(new File(pathToDirectory + "/" + fileName), obj);
	}
	
	@SuppressWarnings("rawtypes")
	public ArangoRepository getRepositoryName(Object objectWithoutIds) {
		var repositories = new Repositories(appContext);
		return (ArangoRepository) repositories.getRepositoryFor(objectWithoutIds.getClass()).orElseThrow();
	}
		
	public static boolean stringContains(String inputStr, String[] items) {
	    return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
	}
	
	private Class<?> getClassbyString(String input) throws ClassNotFoundException {
		Class<?> act = Class.forName("ch.uzh.marugoto.core.data.entity." + StringUtils.capitalize(input));
		return act;
	}
	
	private void doImport(String pathToDirectory) throws InstantiationException, IllegalAccessException, IOException, ParseException, ClassNotFoundException {
		File[] files = getAllFiles(pathToDirectory);
		
    	for (int i =0; i < files.length; i++) {
    		if (!files[i].isDirectory()) {
    			var nameWithoutExtension = files[i].getName().substring(0, files[i].getName().lastIndexOf('.')); // remove extension
    			String name = nameWithoutExtension .replaceAll("\\d", ""); // remove numbers, dots, and whitespaces from string
    			if (name.contains("pageTransition")) {
    				continue;
    			}
    			obj = convertFromJsonToObject(files[i], getClassbyString(name));
    			SAVED_OBJECTS.put(pathToDirectory + "/"+ files[i].getName(), obj); 
				convertFromObjectToJson(obj,pathToDirectory,files[i].getName());	
			} 
    	} 
    	File[] directories = getAllDirectories(pathToDirectory);
    	for (int  j = 0; j < directories.length; j++) {
    		 doImport(directories[j].getAbsolutePath());
    	}
	}
	@SuppressWarnings("unchecked")
	private void setRelations() {
		SAVED_OBJECTS.forEach((key,value) -> {
			
			if (key.contains("page")) {
				Object obj = SAVED_OBJECTS.get(key);
				if (obj instanceof Page) {
					Page page = (Page) value;
					File pageFile = new File(key);
					var chapterFile = pageFile.getParentFile().getParentFile();
					Chapter chapterobj = (Chapter) SAVED_OBJECTS.get(chapterFile.getAbsolutePath() +"/chapter.json");
					var storyline = chapterFile.getParentFile();
					Storyline storylineobj = (Storyline) SAVED_OBJECTS.get(storyline.getAbsolutePath() +"/storyline.json");
					page.setChapter(chapterobj);
					page.setStoryline(storylineobj);
					getRepositoryName(page).save(value);

				} else if (obj instanceof Component) {
					File file = new File(key);
					Page page  = (Page) SAVED_OBJECTS.get(file.getParentFile().getAbsolutePath() + "/page.json");
					Component component = (Component) SAVED_OBJECTS.get(key);
					component.setPage(page);
					componentRepository.save(component);
				}
				
			} 	
		});
	}
	
}
