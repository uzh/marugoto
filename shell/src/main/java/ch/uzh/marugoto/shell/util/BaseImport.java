package ch.uzh.marugoto.shell.util;

import com.arangodb.springframework.core.ArangoOperations;
import com.arangodb.springframework.repository.ArangoRepository;

import org.json.simple.parser.ParseException;
import org.springframework.data.repository.support.Repositories;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;

public class BaseImport {

    private final HashMap<String, Object> OBJECTS_FOR_IMPORT = new HashMap<String, Object>();

    public BaseImport(String pathToFolder) {

        try {
            prepareObjectsForImport(pathToFolder);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public HashMap<String, Object> getObjectsForImport() {
        return OBJECTS_FOR_IMPORT;
    }

    protected void truncateDatabase() {
        var dbConfig = BeanUtil.getBean(DbConfiguration.class);
        var operations = BeanUtil.getBean(ArangoOperations.class);

        System.out.println(String.format("Truncating database `%s`...", dbConfig.database()));

        operations.dropDatabase();
        operations.driver().createDatabase(dbConfig.database());
    }

    /**
     * Import data from generated folder structure
     *
     * @param pathToDirectory
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     * @throws ParseException
     * @throws ClassNotFoundException
     */
    protected void prepareObjectsForImport(String pathToDirectory) throws InstantiationException, IllegalAccessException, IOException, ParseException, ClassNotFoundException {
        File[] files = FileService.getAllFiles(pathToDirectory);

        for (int i =0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                var filePath = files[i].getPath();
                var nameWithoutExtension = files[i].getName().substring(0, files[i].getName().lastIndexOf('.')); // remove extension
                String name = nameWithoutExtension .replaceAll("\\d", ""); // remove numbers, dots, and whitespaces from string
                // skip page transition files
                if (name.contains("pageTransition")) {
                    continue;
                }

                var obj = FileService.generateObjectFromJsonFile(files[i], getEntityClassByName(name));

//                if (importMode.equals("update") && !isUpdateAllowed(obj)) {
//                    System.out.println("Error updating: ID is missing in file " + filePath);
//                    break;
//                }

                OBJECTS_FOR_IMPORT.put(filePath, obj);
//                var object = saveObject(obj);
//                FileService.updateJsonFileFromObject(object, filePath);
            }
        }

        File[] directories = FileService.getAllDirectories(pathToDirectory);
        for (File directory : directories) {
            prepareObjectsForImport(directory.getAbsolutePath());
        }
    }

    protected void saveObjectsRelations() {
        for (Map.Entry<String, Object> entry : OBJECTS_FOR_IMPORT.entrySet()) {
            String filePath = entry.getKey();
            Object obj = entry.getValue();

//            if (filePath.contains("page")) {
                // We are in page folder
//            }
            if (obj instanceof Component) {
                var page = (Page) getImportObjectByFile("page.json");
                var component = (Component) obj;
                component.setPage(page);
                saveObject(component, filePath);
            } else if (obj instanceof NotebookEntry) {
                var page = (Page) getImportObjectByFile("page.json");
                var notebookEntry = (NotebookEntry) obj;
                notebookEntry.setPage(page);
                saveObject(notebookEntry, filePath);
            } else if (obj instanceof Page) {
                var page = (Page) obj;
                var chapter = (Chapter) getImportObjectByFile("chapter.json");
                var storyline = (Storyline) getImportObjectByFile("storyline.json");
                page.setChapter(chapter);
                page.setStoryline(storyline);
                saveObject(page, filePath);
            }
        }
    }

    protected Object saveObject(Object obj, String filePath) {
        var savedObject = getRepository(obj.getClass()).save(obj);
        // update json file
        FileService.updateJsonFileFromObject(savedObject, filePath);
        return savedObject;
    }

    protected Class<?> getEntityClassByName(String input) throws ClassNotFoundException {
        Class<?> className = Class.forName("ch.uzh.marugoto.core.data.entity." + StringUtils.capitalize(input));
        return className;
    }


    @SuppressWarnings("rawtypes")
    protected ArangoRepository getRepository(Class clazz) {
        ArangoRepository repository;
        String[] items = new String[] {"Exercise", "Component"};

        if (clazz.getName().equals("Component") || stringContains(clazz.getName(), items)) {
            repository = BeanUtil.getBean(ComponentRepository.class);
        } else {
            repository = (ArangoRepository) new Repositories(BeanUtil.getContext()).getRepositoryFor(clazz).orElseThrow();
        }

        return repository;
    }

    private static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

    private Object getImportObjectByFile(String jsonFile) {
        Object objectForImport = null;
        for (Map.Entry<String, Object> entry : OBJECTS_FOR_IMPORT.entrySet()) {
            String filePath = entry.getKey();

            if (filePath.contains(jsonFile)) {
                objectForImport = entry.getValue();
                break;
            }
        }

        return objectForImport;
    }
}
