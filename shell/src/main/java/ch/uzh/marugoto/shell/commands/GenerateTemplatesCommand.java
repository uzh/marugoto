package ch.uzh.marugoto.shell.commands;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
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
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.StorylineRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.shell.util.FileGenerator;

@ShellComponent
public class GenerateTemplatesCommand {

	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private StorylineRepository storylineRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	@Autowired
	private ComponentRepository componentRepository;

	@ShellMethod("Generates folder structure and empty json files")
	public void generateTemplateFiles(String destinationPath) {

		var templatesFolder = FileGenerator.generateFolder(destinationPath);

		// TOPIC
		FileGenerator.generateJsonFileFromObject(new Topic(), "topic", templatesFolder);

		// STORY LINE
		var storylineFolder = FileGenerator.generateFolder(templatesFolder.getPath(), "storyline");
		FileGenerator.generateJsonFileFromObject(new Storyline(null, false), "storyline", storylineFolder);

		// PAGES
		var pagesFolder = FileGenerator.generateFolder(storylineFolder.getPath(), "page", 4);

		var page = new Page();
		var pageTransition = new PageTransition(null, null, null);
		var textComponent = new TextComponent(0, null);

		// add page json to each page folder
		for (int i = 0; i < pagesFolder.size(); i++) {
			FileGenerator.generateJsonFileFromObject(page, "page", pagesFolder.get(i));
		}

		var page1 = pagesFolder.get(0);
		FileGenerator.generateJsonFileFromObject(pageTransition, "pageTransition", page1);
		FileGenerator.generateJsonFileFromObject(textComponent, "textComponent", page1);

		var page2 = pagesFolder.get(1);
		FileGenerator.generateJsonFileFromObject(pageTransition, "pageTransition", page2);
		FileGenerator.generateJsonFileFromObject(textComponent, "textComponent", page2);

		var page3 = pagesFolder.get(2);
		FileGenerator.generateJsonFileFromObject(pageTransition, "pageTransition", page3, 3);
		FileGenerator.generateJsonFileFromObject(textComponent, "textComponent", page3);
		FileGenerator.generateJsonFileFromObject(new TextExercise(0, 0, 0, null), "textExercise", page3);
		FileGenerator.generateJsonFileFromObject(new RadioButtonExercise(0, null, null), "radioButtonExercise", page3);

		var page4 = pagesFolder.get(3);
		FileGenerator.generateJsonFileFromObject(textComponent, "textComponent", page4);
	}
}
