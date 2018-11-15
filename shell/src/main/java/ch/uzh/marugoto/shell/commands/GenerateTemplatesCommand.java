package ch.uzh.marugoto.shell.commands;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import afu.org.checkerframework.checker.oigj.qual.O;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.TextComponent;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.Topic;
import ch.uzh.marugoto.shell.util.FileGenerator;

@ShellComponent
public class GenerateTemplatesCommand {

	@ShellMethod("Generates folder structure and empty json files")
	public void generateTemplateFiles(String destinationPath) {

		var templatesFolder = FileGenerator.generateFolder(destinationPath);

		// TOPIC
		FileGenerator.generateJsonFilesFromObject(new Topic(), 1, "topic", templatesFolder);
		// STORY LINES
		var storylineFolder = FileGenerator.generateFolder(templatesFolder.getPath() + File.separator + "storylines");
		FileGenerator.generateJsonFilesFromObject(new Storyline(null, false), 1, "storyline", storylineFolder);

		// PAGES
		var pagesFolder = FileGenerator.generateFolder(templatesFolder.getPath() + File.separator + "pages");
		FileGenerator.generateJsonFilesFromObject(new Page(), 4, "page", pagesFolder);

		// PAGE TRANSITIONS
		var pageTransitionsFolder = FileGenerator.generateFolder(templatesFolder.getPath() + File.separator + "pageTransitions");
		FileGenerator.generateJsonFilesFromObject(new PageTransition(null, null, null), 5, "pageTransition", pageTransitionsFolder);

		// COMPONENTS
		var componentsFolder = FileGenerator.generateFolder(templatesFolder.getPath() + File.separator + "components");
		// Text components
		FileGenerator.generateJsonFilesFromObject(new TextComponent(0, null), 4, "textComponent", componentsFolder);
		// Text exercise
		FileGenerator.generateJsonFilesFromObject(new TextExercise(0, 0, 0, null), 1, "textExercise", componentsFolder);
		// Radio button exercise
		FileGenerator.generateJsonFilesFromObject(new RadioButtonExercise(0, null, null), 1, "radioButtonExercise", componentsFolder);
	}
}
