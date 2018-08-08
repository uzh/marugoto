package ch.uzh.marugoto.backend.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.backend.data.DbConfiguration;
import ch.uzh.marugoto.backend.data.entity.Chapter;
import ch.uzh.marugoto.backend.data.entity.Page;
import ch.uzh.marugoto.backend.data.repository.ChapterRepository;
import ch.uzh.marugoto.backend.data.repository.PageRepository;

@RestController
public class ExampleDateController extends BaseController {
	
	@Autowired
	private ArangoOperations operations;
	
	@Autowired
	private DbConfiguration _dbConfig;
	
	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private PageRepository pageRepository;
	
	@RequestMapping("/createExampleData")
	public String createExampleData() {
		operations.dropDatabase();
		operations.driver().createDatabase(_dbConfig.database());
		
		Log.info(String.format("dev database `%s` truncated.", _dbConfig.database()));
		
		var chapter1 = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));
		var chapter2 = chapterRepository.save(new Chapter("Chapter 2", "icon_chapter_2"));
		
		var page1 = pageRepository.save(new Page("Page 1", true, null));
		var page2 = pageRepository.save(new Page("Page 2", true,chapter1, false, Duration.ofMinutes(30), true, false, false, false));
		var page3 = pageRepository.save(new Page("Page 3", true, chapter2));
		var page4 = pageRepository.save(new Page("Page 4", true, chapter2));
		var page5 = pageRepository.save(new Page("Page 5", true, chapter2));
		
		return "Marugoto example data is created.";
	}
}
