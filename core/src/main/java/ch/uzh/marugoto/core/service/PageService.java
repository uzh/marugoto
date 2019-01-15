package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;

/**
 * PageService provides functionality related to page and pageTransition
 * entities.
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private ComponentService componentService;

	/**
	 * Get page by ID
	 *
	 * @param id
	 * @return page with components
	 */
	public Page getPage(String id) {
		Page page = pageRepository.findById(id).orElseThrow();
		page.setComponents(componentService.getPageComponents(page));
		return page;
	}

	/**
	 * Get start page for specific Topic
	 *
	 *
	 * @return page with components
	 */
	public Page getTopicStartPage() {
		Page page = topicRepository.findAll().iterator().next().getStartPage();
		page.setComponents(componentService.getPageComponents(page));
		return page;
	}
}
