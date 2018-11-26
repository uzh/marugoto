package ch.uzh.marugoto.shell.commands;

import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.repository.ArangoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections.IteratorUtils;
import org.checkerframework.checker.units.qual.C;
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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.shell.util.FileGenerator;

import static java.util.Map.entry;

@ShellComponent
public class DoImportCommand {
	private final String ALLOWED_MODE = "insert,update,override";
	private final HashMap<String, Object> SAVED_OBJECTS = new HashMap<String, Object>();
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
	
	
	@ShellMethod("Updates db from folder structure, path to the {generated} folder should be provided with import mode {insert/update/override}")
	public void doImportStep(String pathToDirectory, String importMode) throws IOException, ParseException, IllegalAccessException, InstantiationException, ClassNotFoundException {

		if (!ALLOWED_MODE.contains(importMode)) {
			System.out.println(String.format("Mode not supported. Allowed: " + ALLOWED_MODE));
			return;
		}

		System.out.println(String.format(StringUtils.capitalize(importMode) + " started..."));

		if (importMode.equals("insert")) {
			System.out.println(String.format("Truncating database `%s`...", dbConfig.database()));
			operations.dropDatabase();
			operations.driver().createDatabase(dbConfig.database());
		}

		doImport(pathToDirectory, importMode);
		synchronizeEntitiesWithStructure(importMode);

		System.out.println(String.format("Finished"));
	}

	/**
	 * Import data from generated folder structure
	 *
	 * @param pathToDirectory
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ClassNotFoundException
	 */
	private void doImport(String pathToDirectory, String importMode) throws InstantiationException, IllegalAccessException, IOException, ParseException, ClassNotFoundException {
		File[] files = FileGenerator.getAllFiles(pathToDirectory);

		for (int i =0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				var filePath = files[i].getPath();
				var nameWithoutExtension = files[i].getName().substring(0, files[i].getName().lastIndexOf('.')); // remove extension
				String name = nameWithoutExtension .replaceAll("\\d", ""); // remove numbers, dots, and whitespaces from string
				// skip page transition files
				if (name.contains("pageTransition")) {
					continue;
				}

				var obj = convertFromJsonToObject(files[i], getClassbyString(name));

				if (importMode.equals("update") && !isUpdateAllowed(obj)) {
					System.out.println("Error updating: " + filePath);
					continue;
				}

				SAVED_OBJECTS.put(filePath, obj);
				var object = saveObject(obj);
				convertFromObjectToJson(object, filePath);
			}
		}

		File[] directories = FileGenerator.getAllDirectories(pathToDirectory);
		for (File directory : directories) {
			doImport(directory.getAbsolutePath(), importMode);
		}
	}

	private boolean isUpdateAllowed(Object obj) {
		boolean allowed = true;
		try {
			Field id;
			if (obj instanceof Component) {
				id = getClassbyString("Component").getDeclaredField("id");
			} else {
				id = obj.getClass().getDeclaredField("id");
			}

			id.setAccessible(true);
			var idValue = id.get(obj);

			if (idValue == null || StringUtils.isEmpty(idValue) || !getRepository(obj).findById(idValue).isPresent()) {
				allowed = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return allowed;
	}

	private void synchronizeEntitiesWithStructure(String importMode) {
		if (importMode.equals("update")) {
			return;
		}

		setEntityRelation();

		if (importMode.equals("override")) {
			removeEntities();
		}
	}

	private void setEntityRelation() {
		for (Map.Entry<String, Object> entry : SAVED_OBJECTS.entrySet()) {
			String filePath = entry.getKey();
			Object obj = entry.getValue();

			if (filePath.contains("page")) {
				// We are in page folder
				if (obj instanceof Component) {
					var page = (Page) getSavedObjectByFolderPath(new File(filePath).getParentFile(), "page");
					var component = (Component) obj;
					component.setPage(page);
					saveObject(component);
				} else if (obj instanceof NotebookEntry) {
					var page = (Page) getSavedObjectByFolderPath(new File(filePath).getParentFile(), "page");
					var notebookEntry = (NotebookEntry) obj;
					notebookEntry.setPage(page);
					saveObject(notebookEntry);
				} else if (obj instanceof Page) {
					var page = (Page) obj;
					var pageFile = new File(filePath);
					var chapterFilePath = pageFile.getParentFile().getParentFile();
					var chapter = (Chapter) getSavedObjectByFolderPath(chapterFilePath, "chapter");
					var storylineFilePath = chapterFilePath.getParentFile();
					var storyline = (Storyline) getSavedObjectByFolderPath(storylineFilePath, "storyline");
					page.setChapter(chapter);
					page.setStoryline(storyline);
					saveObject(page);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void removeEntities() {
		var storylineRepository = getRepository(new Storyline());
		var chapterRepository = getRepository(new Chapter());
		var pageRepository = getRepository(new Page());
		var notebookEntryRepository = getRepository(new NotebookEntry());
		List<Storyline> savedStorylines = IteratorUtils.toList(storylineRepository.findAll().iterator());
		List<Chapter> savedChapters = IteratorUtils.toList(chapterRepository.findAll().iterator());
		List<Page> savedPages = IteratorUtils.toList(pageRepository.findAll().iterator());
		List <NotebookEntry> savedNotebookEntries = IteratorUtils.toList(notebookEntryRepository.findAll().iterator());
		List <Component> savedComponents = IteratorUtils.toList(componentRepository.findAll().iterator());

		for (Map.Entry<String, Object> entry : SAVED_OBJECTS.entrySet()) {
			Object obj = entry.getValue();

			if (savedStorylines.contains(obj)) {
				savedStorylines.remove(obj);
			}

			if (savedChapters.contains(obj)) {
				savedChapters.remove(obj);
			}

			if (savedPages.contains(obj)) {
				savedPages.remove(obj);
			}

			if (savedNotebookEntries.contains(obj)) {
				savedNotebookEntries.remove(obj);
			}

			if (savedComponents.contains(obj)) {
				savedComponents.remove(obj);
			}
		}

		storylineRepository.deleteAll(savedStorylines);
		chapterRepository.deleteAll(savedChapters);
		pageRepository.deleteAll(savedPages);
		notebookEntryRepository.deleteAll(savedNotebookEntries);
		componentRepository.deleteAll(savedComponents);
	}

	private Object getSavedObjectByFolderPath(File destination, String objName) {
		return SAVED_OBJECTS.get(destination.getAbsolutePath() + "/" + objName + ".json");
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
		return mapper.readValue(json, cls); // cast json to java object
	}
	
	private void convertFromObjectToJson(Object obj, String pathToFile) {
		try {
			mapper.writeValue(new File(pathToFile), obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Object saveObject(Object obj) {
		return getRepository(obj).save(obj);
	}

	@SuppressWarnings("rawtypes")
	private ArangoRepository getRepository(Object obj) {
		ArangoRepository repository;
		String[] items = new String[] {"Exercise", "Component"};

		if (stringContains(obj.getClass().getName(), items)) {
			repository = componentRepository;
		} else {
			repository = (ArangoRepository) new Repositories(appContext).getRepositoryFor(obj.getClass()).orElseThrow();
		}

		return repository;
	}
		
	private static boolean stringContains(String inputStr, String[] items) {
	    return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
	}
	
	private Class<?> getClassbyString(String input) throws ClassNotFoundException {
		Class<?> act = Class.forName("ch.uzh.marugoto.core.data.entity." + StringUtils.capitalize(input));
		return act;
	}
}
