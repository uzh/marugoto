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
	
	public Iterable<Page> getAllPages() {
		Iterable<Page> pages = pageRepository.findAll();
		return pages;
	}
	
	public Page getPage(String id) {
		Page page = pageRepository.findById(id).get();
		return page;
	}
}
