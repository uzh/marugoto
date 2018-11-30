package ch.uzh.marugoto.shell.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.util.Map;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.TextComponent;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.shell.util.FileService;

import static java.util.Map.entry;

@ShellComponent
public class GenerateTemplatesCommand {

	private final Map<String, Object> IMPORT_INSTANCES = Map.ofEntries(
			entry("topic", new Topic()),
			entry("storyline", new Storyline()),
			entry("chapter", new Chapter()),
			entry("page", new Page()),
			entry("notebookEntry", new NotebookEntry()),
			entry("textComponent", new TextComponent()),
			entry("textExercise", new TextExercise()),
			entry("radioButtonExercise", new RadioButtonExercise()),
			entry("checkboxExercise", new CheckboxExercise()),
			entry("dateExercise", new DateExercise()),
			entry("pageTransition", new PageTransition()
	));

	@ShellMethod("Generates folder structure and empty json files. Needs import-settings.json. Example is in docs.")
	public void generateTemplateFiles(String destinationPath) {

		var importConfigFile = new File(destinationPath);

		if (importConfigFile.isFile()) {
			importFromJsonFile(importConfigFile);
		} else {
			// try to find the file
			importConfigFile = new File(destinationPath + "/import-config.json");

			if (importConfigFile.exists()) {
				importFromJsonFile(importConfigFile);
			} else {
				System.out.println("PATH ERROR: Path to json file is needed. (Path to folder provided and import-config.json is needed. An example is in docs)");
			}
		}
	}

	private void importFromJsonFile(File file) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
			var storylineKey = jsonObject.keySet().iterator().next().toString();

			if (!storylineKey.equals("storyline")) {
				System.out.println("FILE ERROR: not a valid import config file (json file)");
				return;
			}

			var destinationFolder = FileService.generateFolder(file.getParentFile().getAbsolutePath() + "/generated");
			// TOPIC
			FileService.generateJsonFileFromObject(new Topic(), "topic", destinationFolder);
			// STORY LINES
			generateStorylineTemplates(jsonObject, storylineKey, IMPORT_INSTANCES.get(storylineKey), destinationFolder);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateStorylineTemplates(JSONObject jsonParentObject, String jsonKey, Object entity, File destination) {
		var isValid = IMPORT_INSTANCES.containsKey(jsonKey);
		if (!isValid) {
			System.out.println("FILE ERROR: not a valid json file");
		}

		JSONArray jsonList = (JSONArray) jsonParentObject.get(jsonKey);

		for (int j = 0; j < jsonList.size(); j++) {
			var generatedFolder = FileService.generateFolder(destination.getPath(), jsonKey + (j + 1));

			setEntityTitle(entity, generatedFolder, jsonKey);

			FileService.generateJsonFileFromObject(entity, jsonKey, generatedFolder);
			JSONObject jsonObject = (JSONObject) jsonList.get(j);

			if (jsonKey.equals("page")) {
				for (Object o : jsonObject.keySet()) {
					var property = (String) o;
					var val = (Long) jsonObject.get(property);
					FileService.generateInitialJsonFilesFromObject(IMPORT_INSTANCES.get(property), property, generatedFolder, val.intValue());
				}
			} else {
				var nextJsonKey = jsonObject.keySet().iterator().next().toString();
				generateStorylineTemplates(jsonObject, nextJsonKey, IMPORT_INSTANCES.get(nextJsonKey), generatedFolder);
			}
		}
	}
	
	/**
	 * Set title to entity (used for Storyline/Chapter/Page)
	 * 
	 * @param entity
	 * @param generatedFolder
	 * @param jsonKey
	 */
	private void setEntityTitle(Object entity, File generatedFolder, String jsonKey) {
		if (jsonKey.equals("storyline")) {
			// example Storyline1
			var storylineTitle = StringUtils.capitalize(generatedFolder.getName());
			((Storyline) entity).setTitle(storylineTitle);
		} else if (jsonKey.equals("chapter")) {
			// example Storyline1 Chapter1
			var chapterTitle = StringUtils.capitalize(generatedFolder.getParentFile().getName()) + " " + StringUtils.capitalize(generatedFolder.getName());
			((Chapter) entity).setTitle(chapterTitle);
		} else if (jsonKey.equals("page")) {
			// example Storyline1 Chapter1 Page1
			var pageTitle = StringUtils.capitalize(generatedFolder.getParentFile().getParentFile().getName()) + " " + StringUtils.capitalize(generatedFolder.getParentFile().getName()) + " " + StringUtils.capitalize(generatedFolder.getName());
			((Page) entity).setTitle(pageTitle);
		}
	}
}