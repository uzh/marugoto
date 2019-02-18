 package ch.uzh.marugoto.core.service;

 import org.commonmark.node.Node;
 import org.commonmark.parser.Parser;
 import org.commonmark.renderer.html.HtmlRenderer;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Service;

 import java.util.ArrayList;
 import java.util.List;

 import ch.uzh.marugoto.core.data.entity.application.ComponentResource;
 import ch.uzh.marugoto.core.data.entity.topic.Component;
 import ch.uzh.marugoto.core.data.entity.topic.Page;
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
	 * Find specific component by ID
	 *
	 * @param componentId
	 * @return
	 */
	public Component findById(String componentId) {
		return componentRepository.findById(componentId).orElseThrow();
	}

	/**
	 * Returns all components that belongs to the page
	 *
	 * @param page
	 * @return components
	 */
	public List<Component> getPageComponents(Page page) {
		return componentRepository.findPageComponents(page.getId());
	}

	 /**
	  * Returns all component resources (components with states) for the page
	  *
	  * @param page Page
	  * @return components
	  */
	public List<ComponentResource> getComponentsResources(Page page) {
		List<ComponentResource> resourceList = new ArrayList<>();

		for (Component component : getPageComponents(page)) {
			resourceList.add(new ComponentResource(component));
		}

		return resourceList;
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
