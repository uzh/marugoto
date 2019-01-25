package ch.uzh.marugoto.shell.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import ch.uzh.marugoto.core.data.entity.AudioComponent;
import ch.uzh.marugoto.core.data.entity.AudioResource;
import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Character;
import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.DialogExercise;
import ch.uzh.marugoto.core.data.entity.DialogResponse;
import ch.uzh.marugoto.core.data.entity.DialogSpeech;
import ch.uzh.marugoto.core.data.entity.DocumentResource;
import ch.uzh.marugoto.core.data.entity.ImageComponent;
import ch.uzh.marugoto.core.data.entity.ImageNotebookEntry;
import ch.uzh.marugoto.core.data.entity.ImageResource;
import ch.uzh.marugoto.core.data.entity.LinkComponent;
import ch.uzh.marugoto.core.data.entity.Mail;
import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PdfComponent;
import ch.uzh.marugoto.core.data.entity.PdfNotebookEntry;
import ch.uzh.marugoto.core.data.entity.PdfResource;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.TextComponent;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.core.data.entity.VideoComponent;
import ch.uzh.marugoto.core.data.entity.VideoResource;
import ch.uzh.marugoto.core.data.entity.VirtualTime;
import ch.uzh.marugoto.shell.helpers.FileHelper;

import static java.util.Map.entry;

@ShellComponent
public class GenerateTemplatesCommand {

	private final Map<String, Object> importInstances = Map.ofEntries(
			entry("topic", new Topic()),
			entry("storyline", new Storyline()),
			entry("chapter", new Chapter()),
			entry("page", new Page()),
			entry("pageTransition", new PageTransition()),
			entry("notebookEntry", new NotebookEntry()),
			entry("imageNotebookEntry", new ImageNotebookEntry()),
			entry("pdfNotebookEntry", new PdfNotebookEntry()),
			entry("textComponent", new TextComponent()),
			entry("imageComponent", new ImageComponent()),
			entry("pdfComponent", new PdfComponent()),
			entry("audioComponent", new AudioComponent()),
			entry("videoComponent", new VideoComponent()),
			entry("linkComponent", new LinkComponent()),
			entry("imageResource", new ImageResource()),
			entry("pdfResource", new PdfResource()),
			entry("audioResource", new AudioResource()),
			entry("videoResource", new VideoResource()),
			entry("documentResource", new DocumentResource()),
			entry("textExercise", new TextExercise()),
			entry("radioButtonExercise", new RadioButtonExercise()),
			entry("checkboxExercise", new CheckboxExercise()),
			entry("dateExercise", new DateExercise()),
			entry("mail", new Mail()),
			entry("character", new Character()),
			entry("dialogExercise", new DialogExercise()),
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
			var storylineKey = jsonObject.keySet().iterator().next().toString();

			if (!storylineKey.equals("storyline")) {
				System.out.println("FILE ERROR: not a valid import config file (json file)");
//				return;
			}

			var rootFolder = ch.uzh.marugoto.core.helpers.FileHelper
					.generateFolder(file.getParentFile().getAbsolutePath() + "/generated");

			// TOPIC
			FileHelper.generateJsonFileFromObject(new Topic(), "topic", rootFolder);
			// STORY LINES
			generateStorylineTemplates(jsonObject, storylineKey, importInstances.get(storylineKey), rootFolder);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateStorylineTemplates(JSONObject jsonParentObject, String jsonKey, Object entity, File destination) {
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
				generateStorylineTemplates(jsonObject, nextJsonKey, importInstances.get(nextJsonKey), generatedFolder);
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
			case "storyline":
				// example Storyline1
				var storylineTitle = StringUtils.capitalize(generatedFolder.getName());
				((Storyline) entity).setTitle(storylineTitle);
				break;
			case "chapter":
				// example Storyline1 Chapter1
				var chapterTitle = StringUtils.capitalize(generatedFolder.getParentFile().getName()) + " " + StringUtils.capitalize(generatedFolder.getName());
				((Chapter) entity).setTitle(chapterTitle);
				break;
			case "page":
				// example Storyline1 Chapter1 Page1
				var pageTitle = StringUtils.capitalize(generatedFolder.getParentFile().getParentFile().getName()) + " " + StringUtils.capitalize(generatedFolder.getParentFile().getName()) + " " + StringUtils.capitalize(generatedFolder.getName());
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
				pageTransition.setVirtualTime(new VirtualTime());
				pageTransition.setMoney(new Money());
			}

			FileHelper.generateInitialJsonFilesFromObject(object, property, pageFolder, val.intValue());
		}
	}
}