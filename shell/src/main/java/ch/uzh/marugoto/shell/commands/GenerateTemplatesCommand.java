package ch.uzh.marugoto.shell.commands;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.File;
import java.io.IOException;

import ch.uzh.marugoto.core.data.entity.Storyline;

@ShellComponent
public class GenerateTemplatesCommand {

	private final String templatesPath = System.getProperty("user.home") + File.separator + "import-templates";

	@ShellMethod("Generates folders structure and empty file")
	public void generateTemplateFiles() {
		File templatesDir = new File(templatesPath);

		if (!templatesDir.exists()) {
			templatesDir.mkdir();
		}

		var storylineJsonFile = generateStoryline(i + 1);
	}

	private File generateStoryline(int index) {
		final String folderName = "storylines";
		File storylinesFolder = new File(templatesPath + File.separator + folderName);

		if (!storylinesFolder.exists()) {
			storylinesFolder.mkdirs();
		}

		for (int i = 1; i <= 1; i++) {
			var fileName = "storyline" + i + ".json";
			var storylineJson = new File(storyline.getPath() + File.separator + fileName);

			try {
				new ObjectMapper().writeValue(storylineJson, new Storyline(null, null, null, false));
				System.out.println("Storyline " + index + " json file created");
				return storylineJson;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
