package ch.uzh.marugoto.core.data;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.TextComponent;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.entity.VirtualTime;
import ch.uzh.marugoto.core.data.repository.ChapterRepository;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;


@Service
public class DbSeeders {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ChapterRepository chapterRepository;
		
	@Autowired
	private PageRepository pageRepository;
	
	
	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	@Autowired
	private ComponentRepository componentRepository;
	
	@Autowired
	private PageStateRepository pageStateRepository;
	

	public void createData() {
		var testUser1 = userRepository.save(new User(UserType.Guest, Salutation.Mr, "Fredi", "Kruger", "unittest@marugoto.ch", "test"));
		
		var testChapter1 = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));
		var testChapter2 = chapterRepository.save(new Chapter("Chapter 2", "icon_chapter_2"));


		var testPage1 = new Page("Page 1", true, null);
		var testPage2 = new Page("Page 2", true, testChapter1, false, Duration.ofMinutes(30), true, false, false, false);
		var testPage3 = new Page("Page 3", true, testChapter2); 
		var testPage4 = new Page("Page 4", true, testChapter1);
		
		
		var testComponent1 = componentRepository
				.save(new TextComponent(0, 300, 200, 200, "Some example title", "Some example text for component"));
		
		var testExercise1 = new TextExercise(100, 100, 400, 400, 5, 25, "Wording", "What does 'domo arigato' mean?", null, 20);
		testExercise1.addTextSolution(new TextSolution("Thank you"));	
		testExercise1.addTextSolution(new TextSolution("Thank's"));
		componentRepository.save(testExercise1);

		
		testPage1.addComponent(testComponent1);
		testPage2.addComponent(testExercise1);
		
		testPage4.setTime(new VirtualTime(Duration.ofDays(7), false));
		testPage4.setMoney(new Money(1000, false));

		
		pageRepository.save(testPage1);
		pageRepository.save(testPage2);
		pageRepository.save(testPage3);
		pageRepository.save(testPage4);
		
		var testPageTransition1to2 = new PageTransition(testPage1, testPage2, "confirm");
		var testPageTransition1to3 = new PageTransition(testPage1, testPage3, "submit");
		var testPageTransition2to4 = new PageTransition(testPage2, testPage4, "login");

		
		pageTransitionRepository.save(testPageTransition1to2);
		pageTransitionRepository.save(testPageTransition1to3);
		pageTransitionRepository.save(testPageTransition2to4);
		// States
		var testPageState1 = new PageState(testPage1,testUser1);
		var testPageState2 = new PageState(testPage2,testUser1);
		testPageState2.addExerciseState(new ExerciseState(testExercise1, "Good morning"));

		pageStateRepository.save(testPageState1);
		pageStateRepository.save(testPageState2);

	}
	
}
