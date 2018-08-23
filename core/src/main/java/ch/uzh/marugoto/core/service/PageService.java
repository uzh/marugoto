package ch.uzh.marugoto.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;

/**
 * PageService provides functionality related to page and pageTransition entities.
 */
@Service
public class PageService {

	@Autowired
	private PageRepository pageRepository;
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;



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
