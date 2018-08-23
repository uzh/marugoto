package ch.uzh.marugoto.shell.commands;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.arangodb.springframework.core.ArangoOperations;
import com.google.common.collect.Lists;

import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Money;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.PageTransitionState;
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
import ch.uzh.marugoto.core.data.repository.PageTransitionStateRepository;
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
	private PageRepository pageRepository;

	@Autowired
	private ComponentRepository componentRepository;

	@Autowired
	private PageTransitionRepository pageTransitionRepository;

	@Autowired
	private PageStateRepository pageStateRepository;

	@Autowired
	private PageTransitionStateRepository pageTransitionStateRepository;

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
		var user1 = userRepository.save(new User(UserType.Guest, Salutation.Mr, "Hans", "Muster", "hans@marugoto.com",
				coreConfig.passwordEncoder().encode("test")));
		userRepository.save(new User(UserType.Guest, Salutation.Ms, "Nadine", "Muster", "nadine@marugoto.com",
				coreConfig.passwordEncoder().encode("test")));

		// Chapters
		var chapter1 = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));
		var chapter2 = chapterRepository.save(new Chapter("Chapter 2", "icon_chapter_2"));

		// Pages
		var page1 = new Page("Page 1", true, null);
		var page2 = new Page("Page 2", true, chapter1, false, Duration.ofMinutes(30), true, false, false, false);
		var page3 = new Page("Page 3", true, chapter2);
		var page4 = new Page("Page 4", true, chapter2);
		var page5 = new Page("Page 5", true, chapter2);
		var page6 = new Page("Page 6", true, chapter2);

		// Page components
		var component1 = componentRepository
				.save(new TextComponent(0, 300, 200, 200, "Some example title", "Some example text for component"));
		var exercise1 = new TextExercise(100, 100, 400, 400, 5, 25, "Wording", "What does 'domo arigato' mean?", 20);
		exercise1.addTextSolution(new TextSolution("Thank you"));
		exercise1.addTextSolution(new TextSolution("Thank's"));
		componentRepository.save(exercise1);

		page1.addComponent(component1);
		page2.addComponent(exercise1);

		page6.setTime(new VirtualTime(Duration.ofDays(7), false));
		page6.setMoney(new Money(1000, false));

		pageRepository.save(page1);
		pageRepository.save(page2);
		pageRepository.save(page3);
		pageRepository.save(page4);
		pageRepository.save(page5);
		pageRepository.save(page6);

		// Page state
		pageStateRepository.save(new PageState(page1, user1));

		var pages = Lists.newArrayList(pageRepository.findAll(new Sort(Direction.ASC, "title")));
		var pageTransition1 = new PageTransition(pages.get(0), pages.get(1), null);

		// Page transitions
		pageTransitionRepository.save(pageTransition1);
		pageTransitionRepository.save(new PageTransition(pages.get(0), pages.get(2), null));
		pageTransitionRepository.save(new PageTransition(pages.get(1), pages.get(3), null));
		pageTransitionRepository.save(new PageTransition(pages.get(2), pages.get(3), null));
		pageTransitionRepository.save(new PageTransition(pages.get(3), pages.get(4), null));
		pageTransitionRepository.save(new PageTransition(pages.get(4), pages.get(5), "Shiny button text",
				new VirtualTime(Duration.ofDays(-10), false), new Money(1000, false)));

		// Page transition states
		pageTransitionStateRepository.save(new PageTransitionState(true, true, pageTransition1));
	}
}