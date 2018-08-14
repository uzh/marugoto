package ch.uzh.marugoto.backend.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.backend.data.DbConfiguration;
import ch.uzh.marugoto.backend.data.entity.Chapter;
import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.repository.ChapterRepository;
import ch.uzh.marugoto.backend.data.repository.PageRepository;

/**
 * Creates dummy data in the database, useful for testing (not for unit-tests!).
 * 
 * TODO: Move to Shell-project (to be created).
 */
@RestController
public class ExampleDataController extends BaseController {
	
	@Autowired
	private ArangoOperations operations;
	
	@Autowired
	private DbConfiguration _dbConfig;
	
	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private PageRepository pageRepository;
	
	
	@GetMapping("/createExampleData")
	public String createExampleData() {
		operations.dropDatabase();
		operations.driver().createDatabase(_dbConfig.database());
		
		Log.info(String.format("dev database `%s` truncated.", _dbConfig.database()));
		
		var chapter1 = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));
		var chapter2 = chapterRepository.save(new Chapter("Chapter 2", "icon_chapter_2"));
		
		pageRepository.save(new Page("Page 1", true, null));
		pageRepository.save(new Page("Page 2", true,chapter1, false, Duration.ofMinutes(30), true, false, false, false));
		pageRepository.save(new Page("Page 3", true, chapter2));
		pageRepository.save(new Page("Page 4", true, chapter2));
		pageRepository.save(new Page("Page 5", true, chapter2));
		
		return "Marugoto example data is created.";
	}
}
