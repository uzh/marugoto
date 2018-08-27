package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.repository.ChapterRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple test cases for ChapterRepository.
 */
public class ChapterRepositoryTest extends BaseCoreTest{

	@Autowired
	private ChapterRepository chapterRepository;
	
	@Test
	public void testCreateChapter() throws Exception {
		Chapter chapter = chapterRepository.save(new Chapter("Chapter 1", "icon_chapter_1"));
		assertNotNull(chapter);
		assertEquals("Chapter 1", chapter.getTitle());
	}

}
