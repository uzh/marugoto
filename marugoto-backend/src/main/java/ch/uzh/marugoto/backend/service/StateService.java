
package ch.uzh.marugoto.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.entity.PageState;
import ch.uzh.marugoto.backend.data.entity.User;
import ch.uzh.marugoto.backend.data.repository.PageStateRepository;

/**
 * State service - responsible for application states
 */

@Service
public class StateService {
	
	@Autowired
	private PageStateRepository pageStateRepository;
	
	public PageState createPageStage(Page page, User user) {
		PageState pageState = new PageState(page, user);
		return pageState;
	}
	
	public PageState getPageState(Page page, User user) {
		PageState pageState = pageStateRepository.findByPageAndUser(page.getId(), user.getId());

		if (pageState == null ) {
			pageState = this.createPageStage(page, user);
		}
		return pageState;
	}
}
