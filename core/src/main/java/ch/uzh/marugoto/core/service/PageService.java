package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.repository.PageRepository;

/**
 * PageService provides functionality related to page and pageTransition
 * entities.
 */
@Service
public class PageService {
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private ComponentService componentService;

	/**
	 *
	 * @param id
	 * @return
	 */
	public Page getPage(String id) {
		Page page = pageRepository.findById(id).orElseThrow();
		page.setComponents(componentService.getPageComponents(page));
		return page;
	}
}
