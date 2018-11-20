package ch.uzh.marugoto.shell.commands;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import com.arangodb.springframework.repository.ArangoRepository;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.marugoto.core.data.repository.ComponentRepository;

@ShellComponent
public class DoImportCommand {

	private final HashMap<String, Object>SAVED_OBJECTS = new HashMap<String, Object>();
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private ObjectMapper mapper;
	@Autowired
    private ApplicationContext appContext;
	private Object obj;
	
	@ShellMethod("does insert or update of json file to database")
	public void doImportStep(String pathToDirectory, String insertMode) throws FileNotFoundException, IOException, ParseException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
	
		if (insertMode.equals("insert")) {

			System.out.println(String.format("Insert data to db"));
			doImport(pathToDirectory);
						
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
		File[] files = folder.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return !file.isHidden();
		    }
		});
		return files;
	}
	
	private File[] getAllDirectories(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		File[] files = folder.listFiles(new FileFilter() {
		    @Override
		    public boolean accept(File file) {
		        return !file.isHidden() && file.isDirectory();
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
	
	private void convertFromObjectToJson(Object obj, String pathToDirectory, String fileName) throws JsonGenerationException, JsonMappingException, IOException {
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
	
	private void doImport(String pathToDirectory) throws FileNotFoundException, InstantiationException, IllegalAccessException, IOException, ParseException, ClassNotFoundException {
		File[] files = getAllFiles(pathToDirectory);
		
    	for (int i =0; i < files.length; i++) {
    		if (!files[i].isDirectory()) {
    			var name = files[i].getName().substring(0, files[i].getName().lastIndexOf('.'));
    			if (name.contains("pageTransition")) {
    				continue;
    			}
    			obj = convertFromJsonToObject(files[i], getClassbyString(name));
    			SAVED_OBJECTS.put(pathToDirectory, obj); 
				convertFromObjectToJson(obj,pathToDirectory,files[i].getName());	
			} 
    	} 
    	File[] directories = getAllDirectories(pathToDirectory);
    	for (int  j = 0; j < directories.length; j++) {
    		 doImport(directories[j].getAbsolutePath());
    	}
	}
//	private String execCmd(String cmd) throws java.io.IOException {
//	    @SuppressWarnings("resource")
//		Scanner scanner = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
//	    return scanner.hasNext() ? scanner.next() : "";
//	}
	
}
