package ch.uzh.marugoto.shell.commands;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Module;
import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.TextComponent;
import ch.uzh.marugoto.core.data.entity.TextExercise;
import ch.uzh.marugoto.core.data.entity.TextSolution;
import ch.uzh.marugoto.core.data.entity.TextSolutionMode;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.entity.VirtualTime;
import ch.uzh.marugoto.core.data.repository.ChapterRepository;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ModuleRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.StorylineRepository;
import ch.uzh.marugoto.core.data.repository.StorylineStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;

@ShellComponent
public class SampleDataCommand {

	@Autowired
	private ArangoOperations operations;

	@Autowired
	private CoreConfiguration coreConfig;

	@Autowired
	private DbConfiguration dbConfig;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private StorylineRepository storylineRepository;

	@Autowired
	private StorylineStateRepository storylineStateRepository;

	@Autowired
	private PageRepository pageRepository;

	@Autowired
	private ComponentRepository componentRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	@Autowired
	private PageStateRepository pageStateRepository;
	
	@Autowired
	private ModuleRepository moduleRepository;

	@ShellMethod("Writes sample data to database, useful for UI testing, not for unit-testing!")
	public void createSampleData() {
		System.out.println(String.format("Truncating database `%s`...", dbConfig.database()));

		operations.dropDatabase();
		operations.driver().createDatabase(dbConfig.database());

		System.out.println("Writing data...");

		writeData();

		System.out.println("Data written to database. Finished.");
	}

	private void writeData() {
		// Users
		var user1 = new User(UserType.Guest, Salutation.Ms, "Hans", "Muster", "hans@marugoto.com",
				coreConfig.passwordEncoder().encode("test"));
		userRepository.save(user1);
		
		// Chapters
		var chapter1 = chapterRepository.save(new Chapter("Info", "icon-chapter-1"));
		var chapter2 = chapterRepository.save(new Chapter("Vitamin 2", "icon-chapter-2"));

		// Storylines
		var storyline1 = storylineRepository
				.save(new Storyline("Get to know Vitamin2", "icon-storyline-1", Duration.ofMinutes(10), true));

		// Pages
		var page1 = new Page("Module description 1/2", true, chapter1);
		var page2 = new Page("Module description 2/2", true, chapter1);
		var page3 = new Page("Question about Vitamin2", true, chapter2, storyline1, false, Duration.ofMinutes(60), true, false, true, true);
		var page4 = new Page("End of Story", true, chapter1, storyline1, true);

		var module1 = new Module("Module123", "icon-module-1", true, page2);

		// Page components
		var component1ForPage1 = componentRepository
				.save(new TextComponent(6, "# This is the first info page. Please go to the next info page and you will find out more."));
		//TODO add ImageComponent 

		var component1ForPage2 = componentRepository
				.save(new TextComponent(6, "# This is the storyline of vitamin2. You can learn something about vitamin2. Please start the storyline!"));
//		//TODO add ImageComponent 
		
		var component1ForPage3 = componentRepository
				.save(new TextComponent(7, "# Do you know how many people work at vitamin2?"));
		var component2ForPage3 = componentRepository
				.save(new TextComponent(3, "# Do you know how old vitamin2 is?"));
		var component1ForPage4 = componentRepository
				.save(new TextComponent(6, "# You are finished with the Storyline vitamin2! Thanks for your work!"));

		var exerciseForPage3 = new TextExercise(6, 0, 250, "Add the number of people who work at vitamin2.");
		exerciseForPage3.addTextSolution(new TextSolution("25", TextSolutionMode.fullmatch));
		componentRepository.save(exerciseForPage3);

		page1.addComponent(component1ForPage1);
		page2.addComponent(component1ForPage2);
		page3.addComponent(component1ForPage3);
		page3.addComponent(exerciseForPage3);
		page3.addComponent(component2ForPage3);
		page4.addComponent(component1ForPage4);
//		TODO add RadioButtonExercise
		pageRepository.save(page1);
		pageRepository.save(page2);
		pageRepository.save(page3);
		pageRepository.save(page4);

		moduleRepository.save(module1);
		// Page transitions
		var pageTransition1FromPage1toPage2 = new PageTransition(page1, page2, "Next");
		
		var pageTransition1FromPage2toPage3 = new PageTransition(page2, page3, "Starten mit der Storyline Vitamin2");
		pageTransition1FromPage2toPage3.setMoney(new Money(1000,true));
		pageTransition1FromPage2toPage3.setTime(new VirtualTime(Duration.ofMinutes(90),true));
		
		var pageTransition1FromPage3toPage4 = new PageTransition(page3, page4, "Next to the end and earn 100.00 CHF");
		pageTransition1FromPage3toPage4.setMoney(new Money(100,true)); 		//TODO add available buttons

		var pageTransition2FromPage3toPage4 = new PageTransition(page3, page4, "Next to the end and earn 1 hour");
		pageTransition2FromPage3toPage4.setTime(new VirtualTime(Duration.ofHours(1),true)); //TODO add available buttons
		
		var pageTransition3FromPage3toPage4 = new PageTransition(page3, page4, "Next to the end and earn 1 hour and 100.00 CHF");
		pageTransition3FromPage3toPage4.setTime(new VirtualTime(Duration.ofHours(1),true)); //TODO add available buttons
		pageTransition3FromPage3toPage4.setMoney(new Money(200,true));

		pageTransitionRepository.save(pageTransition1FromPage1toPage2);
		pageTransitionRepository.save(pageTransition1FromPage2toPage3);
		pageTransitionRepository.save(pageTransition1FromPage3toPage4);
		pageTransitionRepository.save(pageTransition2FromPage3toPage4);
		pageTransitionRepository.save(pageTransition3FromPage3toPage4);

//		// StorylineState
//		var testStorylineState1 = new StorylineState(testStoryline1, user1);
//		storylineStateRepository.save(testStorylineState1);
//
//		// Page state
//		var pageState = new PageState(page1, testStorylineState1);
//		pageState.addPageTransitionState(new PageTransitionState(true, pageTransition1));
//		pageState.addPageTransitionState(new PageTransitionState(true, pageTransition2));
//		pageStateRepository.save(pageState);
//
//		testStorylineState1.setCurrentlyAt(pageState);
//		storylineStateRepository.save(testStorylineState1);
//
//		user1.setCurrentlyPlaying(testStorylineState1);
//		userRepository.save(user1);
//
//		// Page transition states
//		pageTransitionStateRepository.save(new PageTransitionState(true, pageTransition1FromPage1toPage2));
//		pageTransitionStateRepository.save(new PageTransitionState(false, pageTransition1FromPage2toPage3));
//		pageTransitionStateRepository.save(new PageTransitionState(true, pageTransition1FromPage3toPage4));
//		pageTransitionStateRepository.save(new PageTransitionState(true, pageTransition2FromPage3toPage4));
//		pageTransitionStateRepository.save(new PageTransitionState(true, pageTransition3FromPage3toPage4));
	}
}