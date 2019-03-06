package ch.uzh.marugoto.core.data;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.MailReply;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.state.PageTransitionState;
import ch.uzh.marugoto.core.data.entity.state.PersonalNote;
import ch.uzh.marugoto.core.data.entity.topic.Chapter;
import ch.uzh.marugoto.core.data.entity.topic.Character;
import ch.uzh.marugoto.core.data.entity.topic.CheckboxExercise;
import ch.uzh.marugoto.core.data.entity.topic.CheckboxSolutionMode;
import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.DateExercise;
import ch.uzh.marugoto.core.data.entity.topic.DateSolution;
import ch.uzh.marugoto.core.data.entity.topic.Dialog;
import ch.uzh.marugoto.core.data.entity.topic.DialogResponse;
import ch.uzh.marugoto.core.data.entity.topic.DialogSpeech;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.ExerciseOption;
import ch.uzh.marugoto.core.data.entity.topic.ImageResource;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.MailCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.Money;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.topic.NotebookEntryAddToPageStateAt;
import ch.uzh.marugoto.core.data.entity.topic.Page;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.RadioButtonExercise;
import ch.uzh.marugoto.core.data.entity.topic.Salutation;
import ch.uzh.marugoto.core.data.entity.topic.TextComponent;
import ch.uzh.marugoto.core.data.entity.topic.TextExercise;
import ch.uzh.marugoto.core.data.entity.topic.TextSolution;
import ch.uzh.marugoto.core.data.entity.topic.TextSolutionMode;
import ch.uzh.marugoto.core.data.entity.topic.Topic;
import ch.uzh.marugoto.core.data.entity.topic.UserType;
import ch.uzh.marugoto.core.data.entity.topic.VirtualTime;
import ch.uzh.marugoto.core.data.repository.ChapterRepository;
import ch.uzh.marugoto.core.data.repository.CharacterRepository;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.DialogResponseRepository;
import ch.uzh.marugoto.core.data.repository.DialogSpeechRepository;
import ch.uzh.marugoto.core.data.repository.ExerciseStateRepository;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.MailStateRepository;
import ch.uzh.marugoto.core.data.repository.NotebookEntryRepository;
import ch.uzh.marugoto.core.data.repository.NotificationRepository;
import ch.uzh.marugoto.core.data.repository.PageRepository;
import ch.uzh.marugoto.core.data.repository.PageStateRepository;
import ch.uzh.marugoto.core.data.repository.PageTransitionRepository;
import ch.uzh.marugoto.core.data.repository.PersonalNoteRepository;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.data.repository.TopicRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;


@Service
public class TestDbSeeders {
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
	@Autowired
	private ExerciseStateRepository exerciseStateRepository;
	@Autowired
	private NotebookEntryRepository notebookEntryRepository;
	@Autowired
	private TopicRepository topicRepository;
	@Autowired
	private PersonalNoteRepository personalNoteRepository;
	@Autowired
	private ResourceRepository resourceRepository;
	@Autowired
	private DialogResponseRepository dialogResponseRepository;
	@Autowired
	private DialogSpeechRepository dialogSpeechRepository;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private CharacterRepository characterRepository;
	@Autowired
	private MailStateRepository mailStateRepository;
	@Autowired
	private GameStateRepository gameStateRepository;


	public void createData() {
		var testUser1 = new User(UserType.Guest, Salutation.Mr, "Fredi", "Kruger", "unittest@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
		var testUser2 = new User(UserType.Supervisor, Salutation.Mr, "Supervisor", "Marugoto", "supervisor@marugoto.ch", new BCryptPasswordEncoder().encode("test"));
		userRepository.save(testUser1);
		userRepository.save(testUser2);

		var testChapter1 = chapterRepository.save(new Chapter("Chapter-1", "icon-chapter-1"));
		var testChapter2 = chapterRepository.save(new Chapter("Chapter-2", "icon-chapter-2"));

		var testPage1 = new Page("Page 1", testChapter1);
		var testPage2 = new Page("Page 2", testChapter1, new VirtualTime(Duration.ofMinutes(30), false), null, false, true, false, false);
		var testPage3 = new Page("Page 3", testChapter2);
		var testPage4 = new Page("Page 4", testChapter1, new VirtualTime(Duration.ofDays(7), false), new Money(1000), false, true, false, false);
		var testPage5 = new Page("Page 5", testChapter2);
		var testPage6 = new Page("Page 6", testChapter2);

		pageRepository.saveAll(List.of(testPage1, testPage2, testPage3, testPage4, testPage5, testPage6));

		var testTopic1 = new Topic("TestTopic", null, true, testPage1);
		topicRepository.save(testTopic1);
		testUser1.setCurrentGameState(gameStateRepository.save(new GameState(testTopic1, testUser1)));
		userRepository.save(testUser1);

		var testComponent1 = new TextComponent(6, "Some example text for component", testPage1);
		testComponent1.setRenderOrder(1);
		var testTextExercise1 = new TextExercise(6, 25, "What does 'domo arigato' mean?", testPage1);
		testTextExercise1.setRenderOrder(2);

		testTextExercise1.addTextSolution(new TextSolution("Thank" ,TextSolutionMode.contains));
		testTextExercise1.addTextSolution(new TextSolution("Thank you", TextSolutionMode.fullMatch));
		testTextExercise1.addTextSolution(new TextSolution("Thans you", TextSolutionMode.fuzzyComparison));
		
		List<ExerciseOption> options = Arrays.asList(
				new ExerciseOption("one"),
				new ExerciseOption("two", true),
				new ExerciseOption("three", true),
				new ExerciseOption("four"));

		var testRadioButtonExercise = new RadioButtonExercise(3, options,testPage2);
		var dateSolution = new DateSolution(LocalDate.of(2002, 02, 02));
		//var dateSolution = new DateSolution("6.12.2001");
		var testDateExercise = new DateExercise(1, true, "This is placeholder text", dateSolution, testPage4);
		var testCheckboxExercise = new CheckboxExercise(3, testPage3);
		testCheckboxExercise.setOptions(options);
		testCheckboxExercise.setSolutionMode(CheckboxSolutionMode.correct);

		componentRepository.save(testComponent1);
		componentRepository.save(testTextExercise1);
		componentRepository.save(testCheckboxExercise);
		componentRepository.save(testRadioButtonExercise);
		componentRepository.save(testDateExercise);

		var notebookEntry1 = new NotebookEntry(testPage1, "Page 1 entry", "This is notebook entry for page 1", NotebookEntryAddToPageStateAt.enter);
		var notebookEntry2 = new NotebookEntry(testPage1, "Page 1 exit entry", "This is exit notebook entry for page 1", NotebookEntryAddToPageStateAt.exit);
		notebookEntryRepository.save(notebookEntry1);
		notebookEntryRepository.save(notebookEntry2);


		// character
		var character = new Character(Salutation.Mr, "Hans", "Marugoto", "dev@mail.com");
		characterRepository.save(character);

		// mail
		var mailPage1 = new Mail("inquiry page 1", "This is Page 1 inquiry email", testPage1, character);
		var mailPage6 = new Mail("inquiry", "This is inquiry email", testPage6, character);
		notificationRepository.save(mailPage1);
		notificationRepository.save(mailPage6);

		var testPageTransition1to2 = new PageTransition(testPage1, testPage2, "from 1 to page 2");
		var testPageTransition1to3 = new PageTransition(testPage1, testPage3, "from 1 to page 3");
		var testPageTransition3to4 = new PageTransition(testPage3, testPage4, "from 3 to page 4");
		var testPageTransition2to4 = new PageTransition(testPage2, testPage4, "from 2 to page 4");

		testPageTransition1to2.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, testTextExercise1));
		testPageTransition1to2.addCriteria(new Criteria(mailPage1, MailCriteriaType.reply));
		testPageTransition2to4.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, testRadioButtonExercise));
		testPageTransition2to4.setMoney(new Money(200));
		testPageTransition3to4.addCriteria(new Criteria(ExerciseCriteriaType.correctInput, testCheckboxExercise));

		pageTransitionRepository.save(testPageTransition1to2);
		pageTransitionRepository.save(testPageTransition1to3);
		pageTransitionRepository.save(testPageTransition3to4);
		pageTransitionRepository.save(testPageTransition2to4);

		// resources
		resourceRepository.save(new ImageResource("/dummy/path"));

		// dialog
		var dialogSpeech1 = dialogSpeechRepository.save(new DialogSpeech("Hey, are you ready for testing?"));
		var dialogSpeech2 = dialogSpeechRepository.save(new DialogSpeech("Alright, concentrate then!"));
		var dialogSpeech3 = dialogSpeechRepository.save(new DialogSpeech("Then, goodbye!"));

		var dialogResponse1 = dialogResponseRepository.save(new DialogResponse(dialogSpeech1, dialogSpeech2, "Yes"));
		var dialogResponse2 = dialogResponseRepository.save(new DialogResponse(dialogSpeech1, dialogSpeech3, "No"));
		var dialogResponse3 = dialogResponseRepository.save(new DialogResponse(dialogSpeech2, dialogSpeech2, "Continue", testPageTransition1to2));

		notificationRepository.save(new Dialog(new VirtualTime(Duration.ofSeconds(15), false), testPage3, character, dialogSpeech1));

		notebookEntryRepository.save(new NotebookEntry(dialogResponse1, "Response 1 Entry", "response 1 selected"));
		notebookEntryRepository.save(new NotebookEntry(dialogResponse2, "Response 2 Entry", "response 2 selected"));
		notebookEntryRepository.save(new NotebookEntry(dialogResponse3, "Response 3 Entry", "response 3 selected"));

		// States
		var testPageState1 = new PageState(testPage1, testUser1.getCurrentGameState());
		var testPageState2 = new PageState(testPage6, testUser1.getCurrentGameState());

		MailState mailState = new MailState(mailPage1, testUser1);
		mailState.addMailReply(new MailReply("bla bla"));
		mailStateRepository.save(mailState);

		testPageState1.addNotebookEntry(notebookEntry1);
		testPageState1.addNotebookEntry(notebookEntry2);
		testPageState1.addPageTransitionState(new PageTransitionState(testPageTransition1to2, false));
		testPageState1.addPageTransitionState(new PageTransitionState(testPageTransition1to3, true));
		
		pageStateRepository.save(testPageState1);
		pageStateRepository.save(testPageState2);

		personalNoteRepository.save(new PersonalNote("Personal Note Text", testPageState1, notebookEntry1));

		testUser1.setCurrentPageState(testPageState1);
		userRepository.save(testUser1);
		
		var exerciseState1 = new ExerciseState(testTextExercise1,"some text");
		exerciseState1.setPageState(testPageState1);
		exerciseStateRepository.save(exerciseState1);
	}
}
