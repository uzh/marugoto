package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;

/**
 * State service - responsible for application states
 */

@Service
public class StateService {
	
	@Autowired
	private PageStateRepository pageStateRepository;
	
	public PageState createPageStage(Page page, User user) {
		PageState pageState = new PageState(page, user);
		pageStateRepository.save(pageState);
		return pageState;
	}
	
	public PageState getPageState(Page page, User user) {
		PageState pageState = pageStateRepository.findByPageAndUser(page.getId(), user.getId());

		if (pageState == null)
			pageState = this.createPageStage(page, user);

		return pageState;
	}
}
