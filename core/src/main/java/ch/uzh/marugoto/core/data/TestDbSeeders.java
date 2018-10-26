package ch.uzh.marugoto.core.data;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.CheckboxExerciseMode;
import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.DateExercise;
import ch.uzh.marugoto.core.data.entity.DateSolution;
import ch.uzh.marugoto.core.data.entity.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.Module;
import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.NotebookEntryCreateAt;
import ch.uzh.marugoto.core.data.entity.Option;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
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
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.StorylineRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;


@Service
public class TestDbSeeders {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChapterRepository chapterRepository;
	@Autowired
	private StorylineRepository storylineRepository;
	@Autowired
	private PageRepository pageRepository;
	@Autowired
	private PageTransitionRepository pageTransitionRepository;
	@Autowired
	private ComponentRepository componentRepository;
	@Autowired
	private PageStateRepository pageStateRepository;
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private NotebookEntryRepository notebookEntryRepository;
	@Autowired
	private ModuleRepository moduleRepository;

	public void createData() {
		var testUser1 = new User(UserType.Guest, Salutation.Mr, "Fredi", "Kruger", "unittest@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
		userRepository.save(testUser1);

		var testChapter1 = chapterRepository.save(new Chapter("Chapter-1", "icon-chapter-1"));
		var testChapter2 = chapterRepository.save(new Chapter("Chapter-2", "icon-chapter-2"));
		
		var testStoryline1 = storylineRepository.save(new Storyline("Storyline-1","icon-storyline-1",Duration.ofMinutes(10),true));
		var testStoryline2 = storylineRepository.save(new Storyline("Storyline-2","icon-storyline-2",Duration.ofMinutes(20),true));

		var testPage1 = new Page("Page 1", true, testChapter1);
		var testPage2 = new Page("Page 2", true, testChapter1, testStoryline1, false, Duration.ofMinutes(30), true, false, false, false);
		var testPage3 = new Page("Page 3", true, testChapter2, testStoryline1, true);
		var testPage4 = new Page("Page 4", true, testChapter1, testStoryline2, false, Duration.ofMinutes(10), true, false, false, false);
		var testPage5 = new Page("Page 5", true, testChapter2, testStoryline1, true);
		
		testPage4.setVirtualTime(new VirtualTime(Duration.ofDays(7), false));
		testPage4.setMoney(new Money(1000));

		pageRepository.save(testPage1);
		pageRepository.save(testPage2);
		pageRepository.save(testPage3);
		pageRepository.save(testPage4);
		pageRepository.save(testPage5);

		var testComponent1 = new TextComponent(6, "Some example text for component", testPage1);
		var testTextExercise1 = new TextExercise(6, 5, 25, "What does 'domo arigato' mean?", testPage1);
		
		var testModule1 = new Module("TestModule", "icon-module-1", true, testPage1);
	
		testTextExercise1.addTextSolution(new TextSolution("Thank",TextSolutionMode.contains));
		testTextExercise1.addTextSolution(new TextSolution("Thank you",TextSolutionMode.fullmatch));
		testTextExercise1.addTextSolution(new TextSolution("Thans you",TextSolutionMode.fuzzyComparison));
		
		List<Option> minSelection = Arrays.asList(new Option("3"), new Option("4"));		
		List<Option> maxSelection = Arrays.asList(new Option("1"), new Option ("3"), new Option ("4"));
		List<Option> options = Arrays.asList(new Option("1"), new Option ("2") ,new Option ("3"), new Option ("4"));
		var testCheckboxExerciseForMax = new CheckboxExercise(2, minSelection, maxSelection, options, CheckboxExerciseMode.maxSelection, testPage2);
		var testCheckboxExerciseForMin = new CheckboxExercise(2, minSelection, maxSelection, options, CheckboxExerciseMode.minSelection, testPage3);

		var testRadioButtonExercise = new RadioButtonExercise(3, options,3, testPage4);
		var dateSolution = new DateSolution(LocalDateTime.of(2018, 12, 6, 12, 32));
		var testDateExercise = new DateExercise(1, true, "This is placeholder text", dateSolution, testPage4);

		componentRepository.save(testComponent1);
		componentRepository.save(testTextExercise1);
		componentRepository.save(testCheckboxExerciseForMax);
		componentRepository.save(testCheckboxExerciseForMin);
		componentRepository.save(testRadioButtonExercise);
		componentRepository.save(testDateExercise);

		pageRepository.save(testPage1);
		pageRepository.save(testPage2);
		pageRepository.save(testPage3);
		pageRepository.save(testPage4);
		
		moduleRepository.save(testModule1);

		var notebookEntry1 = new NotebookEntry(testPage1, "Page 1 entry", "This is notebook entry for page 1", NotebookEntryCreateAt.enter);
		var notebookEntry2 = new NotebookEntry(testPage1, "Page 1 exit entry", "This is exit notebook entry for page 1", NotebookEntryCreateAt.exit);
		notebookEntryRepository.save(notebookEntry1);
		notebookEntryRepository.save(notebookEntry2);
	

		var testPageTransition1to2 = new PageTransition(testPage1, testPage2, "from 1 to page 2");
		var testPageTransition1to3 = new PageTransition(testPage1, testPage3, "from 1 to page 3");
		var testPageTransition3to4 = new PageTransition(testPage3, testPage4, "from 3 to page 4");
		var testPageTransition2to4 = new PageTransition(testPage2, testPage4, "from 2 to page 4");

		testPageTransition1to2.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, testTextExercise1));
		testPageTransition2to4.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, testCheckboxExerciseForMax));
		testPageTransition2to4.setMoney(new Money(200));
		testPageTransition3to4.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, testCheckboxExerciseForMin));

		pageTransitionRepository.save(testPageTransition1to2);
		pageTransitionRepository.save(testPageTransition1to3);
		pageTransitionRepository.save(testPageTransition3to4);
		pageTransitionRepository.save(testPageTransition2to4);

		// States
		var testPageState1 = new PageState(testPage1, testUser1);
		
		testPageState1.addNotebookEntry(notebookEntry1);
		testPageState1.addNotebookEntry(notebookEntry2);
		testPageState1.addPageTransitionState(new PageTransitionState(testPageTransition1to2, true));
		testPageState1.addPageTransitionState(new PageTransitionState(testPageTransition1to3));
		
		pageStateRepository.save(testPageState1);

		testUser1.setCurrentPageState(testPageState1);
		
		userRepository.save(testUser1);
		
		var exerciseState1 = new ExerciseState(testTextExercise1,"input text");
		exerciseState1.setPageState(testPageState1);
		exerciseStateRepository.save(exerciseState1);
	}
}
