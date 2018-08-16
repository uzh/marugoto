package ch.uzh.marugoto.backend.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.TextComponent;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.TextComponentRepository;

/**
 * page service is responsible for all actions arround the pages
 * 
 * @author Christian
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private TextComponentRepository textComponentRepository;

	public Iterable<Page> getAllPages() {
		Iterable<Page> pages = pageRepository.findAll();
		if (pages.iterator().hasNext()) {
			pages.forEach(page -> {
				page.setComponents(this.getPageComponents(page));
			});
		}
		return pages;
	}

	/**
	 * Get page with all the belonging components
	 * 
	 * @param id
	 * @return Page
	 */
	public Page getPage(String id) {
		Page page = pageRepository.findById("page/" + id).get();

		// Add related components to the page object
		page.setComponents(this.getPageComponents(page));
		return page;
	}

	/**
	 * Finds the related page components
	 * 
	 * @param page
	 * @return
	 */
	public List<Object> getPageComponents(Page page) {
		Iterable<TextComponent> textComponents = textComponentRepository.findByPage(page.getId());

		List<Object> components = new ArrayList<Object>();
		components.add(textComponents);

		return components;
	}
}
