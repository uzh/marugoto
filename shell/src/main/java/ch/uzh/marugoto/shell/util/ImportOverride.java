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

public class ImportOverride extends BaseImport implements Importer {

    private List<Storyline> savedStorylines;
    private List<Chapter> savedChapters;
    private List<Page> savedPages;
    private List<NotebookEntry> savedNotebookEntries;
    private List<Component> savedComponents;

    public ImportOverride(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        getAllSavedObjects();
        saveObjectsToDatabase();
        saveObjectsRelations();
        removeMissingObjects();
    }

    @SuppressWarnings("unchecked")
	private void getAllSavedObjects() {
        savedStorylines = IteratorUtils.toList(getRepository(Storyline.class).findAll().iterator());
        savedChapters = IteratorUtils.toList(getRepository(Chapter.class).findAll().iterator());
        savedPages = IteratorUtils.toList(getRepository(Page.class).findAll().iterator());
        savedNotebookEntries = IteratorUtils.toList(getRepository(NotebookEntry.class).findAll().iterator());
        savedComponents = IteratorUtils.toList(getRepository(Component.class).findAll().iterator());
    }

    private void saveObjectsToDatabase() {
        for (Map.Entry<String, Object> entry : getObjectsForImport().entrySet()) {
            var filePath = entry.getKey();
            var obj = entry.getValue();
            saveObject(entry.getValue(), filePath);

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
    }

    @SuppressWarnings("unchecked")
	private void removeMissingObjects() {
        getRepository(Storyline.class).deleteAll(savedStorylines);
        getRepository(Chapter.class).deleteAll(savedChapters);
        getRepository(Page.class).deleteAll(savedPages);
        getRepository(NotebookEntry.class).deleteAll(savedNotebookEntries);
        getRepository(Component.class).deleteAll(savedComponents);
        getRepository(PageState.class).deleteAll();
        getRepository(StorylineState.class).deleteAll();
        getRepository(ExerciseState.class).deleteAll();
    }
}
