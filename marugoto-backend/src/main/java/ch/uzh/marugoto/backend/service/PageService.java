package ch.uzh.marugoto.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.repository.PageRepository;

/**
 * page service is responsible for all actions arround the pages
 * 
 * @author Christian
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;
<<<<<<< HEAD
=======
	@Autowired
	private TextComponentRepository textComponentRepository;
>>>>>>> branch 'dev' of git@github.com:uzh/marugoto.git

	public Iterable<Page> getAllPages() {
		Iterable<Page> pages = pageRepository.findAll();
		return pages;
	}

	/**
	 * Get page with all the belonging components
	 * 
	 * @param id
	 * @return Page
	 */
	public Page getPage(String id) {
<<<<<<< HEAD
		Page page = pageRepository.findById("page/"+id).get();
=======
		Page page = pageRepository.findById("page/" + id).get();

		// Add related components to the page object
		page.setComponents(this.getPageComponents(page));
>>>>>>> branch 'dev' of git@github.com:uzh/marugoto.git
		return page;
	}
<<<<<<< HEAD
=======

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
>>>>>>> branch 'dev' of git@github.com:uzh/marugoto.git
}
