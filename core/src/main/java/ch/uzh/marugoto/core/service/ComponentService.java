 package ch.uzh.marugoto.core.service;

import java.util.List;
import java.util.stream.Collectors;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.DialogExercise;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;

/**
 * 
 * Base Service for all components
 *
 */
@Service
public class ComponentService {

	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private DialogService dialogService;

	/**
	 * Returns all the components that belong to page
	 * if one of the components is DialogExercise, it will add exercise answers
	 *
	 * @param page
	 * @return components
	 */
	public List<Component> getPageComponents(Page page) {
		List<Component> components = componentRepository.findByPageId(page.getId());

		for (var component : components) {
			if (component instanceof DialogExercise) {
				var dialogExercise = (DialogExercise) component;
				dialogExercise.setAsnwers(dialogService.getResponseForDialogSpeech(dialogExercise.getSpeech()));
			}
		}

		return components;
	}
	
	/**
	 * Converts MarkDown text to html text
	 * 
	 * @param markdownText
	 * @return htmlOutput
	 */
	public String parseMarkdownToHtml(String markdownText) {
		
		String htmlOutput;
		Parser parser = Parser.builder().build();
		Node document = parser.parse(markdownText);
		HtmlRenderer renderer = HtmlRenderer.builder().build();
		htmlOutput =  renderer.render(document); 
		return htmlOutput;
	}
}
