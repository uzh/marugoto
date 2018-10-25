package ch.uzh.marugoto.core.service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import ch.uzh.marugoto.core.data.entity.Component;
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


	/**
	 * Returns all the components that belong to page
	 *
	 * @param page
	 * @return components
	 */
	public List<Component> getPageComponents(Page page) {
		return componentRepository.findByPageId(page.getId());
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
