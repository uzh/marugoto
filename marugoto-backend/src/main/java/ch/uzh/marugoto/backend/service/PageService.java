package ch.uzh.marugoto.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.PageTransition;
import ch.uzh.marugoto.backend.data.repository.PageRepository;
import ch.uzh.marugoto.backend.data.repository.PageTransitionRepository;

/**
 * PageService assamble the page and pageTransitions as needed by the api. And it holds the business logic.
 * 
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;


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
		Page page = pageRepository.findById(id).get();
		return page;
	}
	
	/**
	 * Get pageTranistions for a page
	 * 
	 * @param id
	 * @return List<PageTransition>
	 */
	public List<PageTransition> getPageTransitions(String id) {
		List<PageTransition> pageTransitions = pageTransitionRepository.getPageTransitionsByPageId(id);
		return pageTransitions;
	}
}
