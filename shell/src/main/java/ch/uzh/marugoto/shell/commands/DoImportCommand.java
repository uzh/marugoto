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
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
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
	
	
	@ShellMethod("Updates db from folder structure, path to the {generated} folder should be provided with import mode {insert/update/override}")
	public void doImportStep(String pathToDirectory, String importMode) throws IOException, ParseException, IllegalAccessException, InstantiationException, ClassNotFoundException {
	
		if (importMode.equals("insert")) {
			System.out.println(String.format("Truncating database `%s`...", dbConfig.database()));
			operations.dropDatabase();
			operations.driver().createDatabase(dbConfig.database());
			System.out.println(String.format("Preparing to insert data to db.."));
			doImport(pathToDirectory);
			System.out.println(String.format("Documents are inserted. Adding relations.."));
			setRelations();
			System.out.println(String.format("Relations are added between documents. Finished"));
		}
		else if (importMode.equals("update")) {
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
				// We are in page folder
				Object obj = SAVED_OBJECTS.get(key);
				if (obj instanceof Page) {
					Page page = (Page) obj;
					File pageFile = new File(key);
					var chapterFile = pageFile.getParentFile().getParentFile();
					Chapter chapterobj = (Chapter) getSavedObjectByFolderPath(chapterFile, "chapter");
					var storyline = chapterFile.getParentFile();
					Storyline storylineobj = (Storyline) getSavedObjectByFolderPath(storyline, "storyline");
					page.setChapter(chapterobj);
					page.setStoryline(storylineobj);
					getRepositoryName(page).save(page);

				} else if (obj instanceof Component) {
					Page page  = (Page) getSavedObjectByFolderPath(new File(key).getParentFile(), "page");
					Component component = (Component) obj;
					component.setPage(page);
					componentRepository.save(component);
				} else if (obj instanceof NotebookEntry) {
					Page page  = (Page) getSavedObjectByFolderPath(new File(key).getParentFile(), "page");
					NotebookEntry entry = (NotebookEntry) obj;
					entry.setPage(page);
					getRepositoryName(entry).save(entry);
				}
			} 	
		});
	}
	
	private Object getSavedObjectByFolderPath(File destination, String objName) {
		return SAVED_OBJECTS.get(destination.getAbsolutePath() + "/" + objName + ".json");
	}
	
}
