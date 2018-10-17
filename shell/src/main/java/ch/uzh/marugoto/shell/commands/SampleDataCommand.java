package ch.uzh.marugoto.shell.commands;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.CheckboxExerciseMode;
import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Module;
import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.Option;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.RadioButtonExercise;
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
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.ModuleRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.StorylineRepository;
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
	private PageRepository pageRepository;

	@Autowired
	private ComponentRepository componentRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	
	@Autowired
	private ModuleRepository moduleRepository;
	
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	
	@Autowired
	private PageStateRepository pageStateRepository;

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

		var module1 = new Module("Module123", "icon-module-1", true, page1);

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


		List<Option> minSelection = Arrays.asList(new Option("1"), new Option("2"));		
		List<Option> maxSelection = Arrays.asList(new Option("1"), new Option ("2") ,new Option ("3"), new Option ("4"));
		List<Option> options = Arrays.asList(new Option("1"), new Option ("2") ,new Option ("3"), new Option ("4"));
		var checkboxExerciseForPage1 = new CheckboxExercise(2, minSelection, maxSelection, options, CheckboxExerciseMode.maxSelection);
		
		List<Option> optionsForRadioExercisePage3 = Arrays.asList(new Option("2 years old"), new Option ("5 years old") ,new Option ("10 years old"));
		var radioButtonExerciseForPage3 = new RadioButtonExercise(3, optionsForRadioExercisePage3, 3);
		
		var textExerciseForPage3 = new TextExercise(6, 0, 250, "Add the number of people who work at vitamin2.");
		textExerciseForPage3.addTextSolution(new TextSolution("25", TextSolutionMode.fullmatch));
		componentRepository.save(textExerciseForPage3);
		componentRepository.save(checkboxExerciseForPage1);
		componentRepository.save(radioButtonExerciseForPage3);

		page1.addComponent(component1ForPage1);
		page1.addComponent(checkboxExerciseForPage1);
		page2.addComponent(component1ForPage2);
		page3.addComponent(component1ForPage3);
		page3.addComponent(textExerciseForPage3);
		page3.addComponent(component2ForPage3);
		page3.addComponent(radioButtonExerciseForPage3);
		page4.addComponent(component1ForPage4);

		pageRepository.save(page1);
		pageRepository.save(page2);
		pageRepository.save(page3);
		pageRepository.save(page4);

		moduleRepository.save(module1);
		// Page transitions
		var pageTransition1FromPage1toPage2 = new PageTransition(page1, page2, "Next");
		
		var pageTransition1FromPage2toPage3 = new PageTransition(page2, page3, "Start with the storyline Vitamin2");
		pageTransition1FromPage2toPage3.setMoney(new Money(1000));
		pageTransition1FromPage2toPage3.setVirtualTime(new VirtualTime(Duration.ofMinutes(90),true));
		
		var pageTransition1FromPage3toPage4 = new PageTransition(page3, page4, "Next to the end and earn 100.00 CHF");
		pageTransition1FromPage3toPage4.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, radioButtonExerciseForPage3));
		pageTransition1FromPage3toPage4.setMoney(new Money(100));

		var pageTransition2FromPage3toPage4 = new PageTransition(page3, page4, "Next to the end and earn 1 hour");
		pageTransition2FromPage3toPage4.setVirtualTime(new VirtualTime(Duration.ofHours(1),true));
		
		var pageTransition3FromPage3toPage4 = new PageTransition(page3, page4, "Next to the end and earn 1 hour and 100.00 CHF");
		pageTransition3FromPage3toPage4.setVirtualTime(new VirtualTime(Duration.ofHours(1),true));
		pageTransition3FromPage3toPage4.setMoney(new Money(200));
		pageTransition3FromPage3toPage4.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, textExerciseForPage3));

		pageTransitionRepository.save(pageTransition1FromPage1toPage2);
		pageTransitionRepository.save(pageTransition1FromPage2toPage3);
		pageTransitionRepository.save(pageTransition1FromPage3toPage4);
		pageTransitionRepository.save(pageTransition2FromPage3toPage4);
		pageTransitionRepository.save(pageTransition3FromPage3toPage4);
		
		var testPageState1 = new PageState(page1, user1);
		var testPageState2 = new PageState(page2, user1);
		pageStateRepository.save(testPageState1);
		pageStateRepository.save(testPageState2);

		var exerciseState1 = new ExerciseState(checkboxExerciseForPage1,"1,2,3,4");
		var exerciseState2 = new ExerciseState(textExerciseForPage3,"25");
		exerciseState1.setPageState(testPageState1);
		exerciseState2.setPageState(testPageState2);
		exerciseStateRepository.save(exerciseState1);
		exerciseStateRepository.save(exerciseState2);
	}
	
}