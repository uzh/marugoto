package ch.uzh.marugoto.shell.util;

import org.apache.commons.collections.IteratorUtils;

import java.util.List;
import java.util.Map;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.ExerciseState;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageState;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.entity.StorylineState;
import ch.uzh.marugoto.core.data.entity.TextComponent;

public class ImportOverride extends BaseImport implements Importer {

    public ImportOverride(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        saveObjectsToDatabase();
        saveObjectsRelations();
        removeObjectsFromDatabase();
    }

    private void saveObjectsToDatabase() {
        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            saveObject(entry.getValue(), entry.getKey());
        }
    }

    private void removeObjectsFromDatabase() {
        var storylineRepository = getRepository(Storyline.class);
        var chapterRepository = getRepository(Chapter.class);
        var pageRepository = getRepository(Page.class);
        var notebookEntryRepository = getRepository(NotebookEntry.class);
        var componentRepository = getRepository(Component.class);

        List<Storyline> savedStorylines = IteratorUtils.toList(storylineRepository.findAll().iterator());
        List<Chapter> savedChapters = IteratorUtils.toList(chapterRepository.findAll().iterator());
        List<Page> savedPages = IteratorUtils.toList(pageRepository.findAll().iterator());
        List <NotebookEntry> savedNotebookEntries = IteratorUtils.toList(notebookEntryRepository.findAll().iterator());
        List <Component> savedComponents = IteratorUtils.toList(componentRepository.findAll().iterator());

        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            Object obj = entry.getValue();

            if (savedStorylines.contains(obj)) {
                savedStorylines.remove(obj);
            }

            if (savedChapters.contains(obj)) {
                savedChapters.remove(obj);
            }

            if (savedPages.contains(obj)) {
                savedPages.remove(obj);
            }

            if (savedNotebookEntries.contains(obj)) {
                savedNotebookEntries.remove(obj);
            }

            if (savedComponents.contains(obj)) {
                savedComponents.remove(obj);
            }
        }

        storylineRepository.deleteAll(savedStorylines);
        chapterRepository.deleteAll(savedChapters);
        pageRepository.deleteAll(savedPages);
        notebookEntryRepository.deleteAll(savedNotebookEntries);
        componentRepository.deleteAll(savedComponents);
        getRepository(PageState.class).deleteAll();
        getRepository(StorylineState.class).deleteAll();
        getRepository(ExerciseState.class).deleteAll();
    }
}
