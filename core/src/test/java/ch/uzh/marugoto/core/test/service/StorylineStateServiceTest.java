package ch.uzh.marugoto.core.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.VirtualTime;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.StorylineStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.service.StorylineStateService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class StorylineStateServiceTest extends BaseCoreTest {
	
	@Autowired
	private StorylineStateRepository storylineStateRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	@Autowired
	private StorylineStateService storylineStateService; 
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Test
	public void testInitializeStateForNewPageIfStorylineStateIsEmpty() {
		
        User user = userRepository.findByMail("unittest@marugoto.ch");
		var page = pageRepository.findByTitle("Page 2");
		PageState currentPageState = new PageState(page, user);
		pageStateRepository.save(currentPageState);
		user.setCurrentPageState(currentPageState);
		userRepository.save(user);
        storylineStateService.initializeStateForNewPage(user);
        var storylineState = user.getCurrentStorylineState();
        assertNotNull(storylineState.getStartedAt());
	}
	
	@Test
	public void testInitializeStateForNewPageIfStorylineStateIsNotEmpty() {
		
        User user = userRepository.findByMail("unittest@marugoto.ch");
		var page = pageRepository.findByTitle("Page 2");
		PageState currentPageState = new PageState(page, user);
		pageStateRepository.save(currentPageState);
		
		StorylineState currentStorylineState = new StorylineState(page.getStoryline());
		storylineStateRepository.save(currentStorylineState);
		user.setCurrentStorylineState(currentStorylineState);
		userRepository.save(user);
		
        storylineStateService.initializeStateForNewPage(user);
        var storylineState = user.getCurrentStorylineState();
        assertNotNull(storylineState.getStartedAt());
        assertNotNull(currentStorylineState.getFinishedAt());
	}
	
	@Test
	public void testUpdateMoneyAndTimeBalanceInStorylineState () throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		
		double starterAmount = 15.0;	
		var page = pageRepository.findByTitle("Page 2");
		var pageTransition = pageTransitionRepository.findByPageId(page.getId()).get(0);
		
		StorylineState storylineState = new StorylineState(page.getStoryline());
		storylineState.setMoneyBalance(starterAmount);
		storylineState.setVirtualTimeBalance(Duration.ZERO);
		storylineStateRepository.save(storylineState);
		
 		pageTransition.setMoney(new Money(starterAmount));
		pageTransition.setVirtualTime(new VirtualTime(Duration.ofMinutes(20), true));
		pageTransitionRepository.save(pageTransition);
		
		storylineStateService.updateVirtualTimeAndMoney(pageTransition.getVirtualTime(), pageTransition.getMoney(), storylineState);
		assertThat(storylineState.getMoneyBalance(), is(starterAmount + pageTransition.getMoney().getAmount()));
		assertThat(storylineState.getVirtualTimeBalance(), is(Duration.ZERO.plus(pageTransition.getVirtualTime().getTime())));	
	}
}
