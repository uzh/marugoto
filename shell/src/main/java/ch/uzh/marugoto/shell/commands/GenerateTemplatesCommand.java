package ch.uzh.marugoto.shell.commands;

import static java.util.Map.entry;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import ch.uzh.marugoto.core.data.entity.topic.AudioComponent;
import ch.uzh.marugoto.core.data.entity.topic.AudioResource;
import ch.uzh.marugoto.core.data.entity.topic.Chapter;
import ch.uzh.marugoto.core.data.entity.topic.Character;
import ch.uzh.marugoto.core.data.entity.topic.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.DateExercise;
import ch.uzh.marugoto.core.data.entity.topic.Dialog;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.DialogSpeech;
import ch.uzh.marugoto.core.data.entity.topic.DocumentResource;
import ch.uzh.marugoto.core.data.entity.topic.ImageComponent;
import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.LinkComponent;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.Money;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.topic.TextComponent;
import ch.uzh.marugoto.core.data.entity.topic.TextExercise;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.UploadExercise;
import ch.uzh.marugoto.core.data.entity.topic.VideoComponent;
import ch.uzh.marugoto.core.data.entity.topic.VideoResource;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;
import ch.uzh.marugoto.shell.helpers.FileHelper;

@ShellComponent
public class GenerateTemplatesCommand {

	private final Map<String, Object> importInstances = Map.ofEntries(
			entry("topic", new Topic()),
			entry("chapter", new Chapter()),
			entry("page", new Page()),
			entry("pageTransition", new PageTransition()),
			entry("notebookEntry", new NotebookEntry()),
			entry("textComponent", new TextComponent()),
			entry("imageComponent", new ImageComponent()),
			entry("audioComponent", new AudioComponent()),
			entry("videoComponent", new VideoComponent()),
			entry("linkComponent", new LinkComponent()),
			entry("imageResource", new ImageResource()),
			entry("audioResource", new AudioResource()),
			entry("videoResource", new VideoResource()),
			entry("documentResource", new DocumentResource()),
			entry("textExercise", new TextExercise()),
			entry("radioButtonExercise", new RadioButtonExercise()),
			entry("checkboxExercise", new CheckboxExercise()),
			entry("dateExercise", new DateExercise()),
			entry("uploadExercise", new UploadExercise()),
			entry("mail", new Mail()),
			entry("character", new Character()),
			entry("dialog", new Dialog()),
			entry("dialogSpeech", new DialogSpeech()),
			entry("dialogResponse", new DialogResponse())
	);

	@ShellMethod("Generates folder structure and empty json files. Needs import-config.json.")
	public void generateTemplateFiles(String configPath) {

		var importConfigFile = new File(configPath);

		if (importConfigFile.isFile()) {
			importFromJsonFile(importConfigFile);
		} else {
			// try to find the file
			importConfigFile = new File(configPath + "/import-config.json");

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
			var chapterKey = jsonObject.keySet().iterator().next().toString();

			if (!chapterKey.contains("chapter")) {
				throw new RuntimeException("FILE ERROR: not a valid import config file (json file)");
			}

			var rootFolder = ch.uzh.marugoto.core.helpers.FileHelper
					.generateFolder(file.getParentFile().getAbsolutePath() + File.separator + "generated");

			// TOPIC
			FileHelper.generateJsonFileFromObject(new Topic(), "topic", rootFolder);
			// CHAPTERS
			generateTopicChapters(jsonObject, chapterKey, importInstances.get(chapterKey), rootFolder);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateTopicChapters(JSONObject jsonParentObject, String jsonKey, Object entity, File destination) {
		var isValid = importInstances.containsKey(jsonKey);
		if (!isValid) {
			System.out.println("FILE ERROR: not a valid json file");
		}

		JSONArray jsonList = (JSONArray) jsonParentObject.get(jsonKey);

		for (int j = 0; j < jsonList.size(); j++) {
			var generatedFolder = ch.uzh.marugoto.core.helpers.FileHelper
					.generateFolder(destination.getPath(), jsonKey + (j + 1));

			setEntityTitle(entity, generatedFolder, jsonKey);

			FileHelper.generateJsonFileFromObject(entity, jsonKey, generatedFolder);
			JSONObject jsonObject = (JSONObject) jsonList.get(j);

			if (jsonKey.equals("page")) {
				// we are now generating files inside page folder
				generatePageRelatedFiles(jsonObject, generatedFolder);
			} else {
				var nextJsonKey = jsonObject.keySet().iterator().next().toString();
				generateTopicChapters(jsonObject, nextJsonKey, importInstances.get(nextJsonKey), generatedFolder);
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
		switch (jsonKey) {
			case "chapter":
				// example Chapter1
				var chapterTitle = StringUtils.capitalize(generatedFolder.getName());
				((Chapter) entity).setTitle(chapterTitle);
				break;
			case "page":
				// example Chapter1 Page1
				var pageTitle = StringUtils.capitalize(generatedFolder.getParentFile().getName()) + " " + StringUtils.capitalize(generatedFolder.getName());
				((Page) entity).setTitle(pageTitle);
				break;
		}
	}

	private void generatePageRelatedFiles(JSONObject jsonObject, File pageFolder) {
		for (Object o : jsonObject.keySet()) {
			var property = (String) o;
			var val = (Long) jsonObject.get(property);
			var object = importInstances.get(property);

			if (object instanceof PageTransition) {
				var pageTransition = (PageTransition) object;
				pageTransition.setCriteria(List.of(new Criteria()));
				pageTransition.setTime(new VirtualTime());
				pageTransition.setMoney(new Money());
			}

			FileHelper.generateInitialJsonFilesFromObject(object, property, pageFolder, val.intValue());
		}
	}
}