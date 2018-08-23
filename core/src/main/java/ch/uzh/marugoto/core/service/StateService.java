
package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;

/**
 * State service - responsible for application states
 */

@Service
public class StateService {
	
	@Autowired
	private PageStateRepository pageStateRepository;
	
	public PageState getPageState(String pageId) {
		PageState pageState = pageStateRepository.findByPage(pageId);
		return pageState;
	}
}
