package ch.uzh.marugoto.shell.util;

import com.arangodb.springframework.repository.ArangoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.data.repository.support.Repositories;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ch.uzh.marugoto.core.data.entity.Chapter;
import ch.uzh.marugoto.core.data.entity.Component;
import ch.uzh.marugoto.core.data.entity.Criteria;
import ch.uzh.marugoto.core.data.entity.DateSolution;
import ch.uzh.marugoto.core.data.entity.DialogSpeech;
import ch.uzh.marugoto.core.data.entity.ImageComponent;
import ch.uzh.marugoto.core.data.entity.NotebookEntry;
import ch.uzh.marugoto.core.data.entity.Page;
import ch.uzh.marugoto.core.data.entity.PageTransition;
import ch.uzh.marugoto.core.data.entity.Resource;
import ch.uzh.marugoto.core.data.entity.Storyline;
import ch.uzh.marugoto.core.data.repository.ComponentRepository;
import ch.uzh.marugoto.core.data.repository.ResourceRepository;
import ch.uzh.marugoto.core.service.FileService;
import ch.uzh.marugoto.shell.deserializer.CriteriaDeserializer;
import ch.uzh.marugoto.shell.deserializer.DateSolutionDeserializer;
import ch.uzh.marugoto.shell.deserializer.ImageComponentDeserializer;

public class BaseImport {

    private final HashMap<String, Object> objectsForImport = new HashMap<>();
    private String folderPath;
    private ObjectMapper mapper;

    public BaseImport(String pathToFolder) {
        try {
            mapper = FileService.getMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Criteria.class, new CriteriaDeserializer());
            module.addDeserializer(ImageComponent.class, new ImageComponentDeserializer());
            module.addDeserializer(DateSolution.class, new DateSolutionDeserializer());
            mapper.registerModule(module);

            folderPath = pathToFolder;
            prepareObjectsForImport(pathToFolder);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected File getRootFolder() {
        return new File(folderPath);
    }

    protected HashMap<String, Object> getObjectsForImport() {
        return objectsForImport;
    }

    /**
     * Import data from generated folder structure
     *
     * @param pathToDirectory
     * @throws IOException
     * @throws ClassNotFoundException
     */
    protected void prepareObjectsForImport(String pathToDirectory) throws IOException, ClassNotFoundException {

        for (var file : FileService.getAllFiles(pathToDirectory)) {
            var filePath = file.getPath();
            var nameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.')); // remove extension
            String name = nameWithoutExtension .replaceAll("\\d", ""); // remove numbers, dots, and whitespaces from string
            Object obj;
            System.out.println("Preparing:" + filePath);
            // skip page transition if json is not valid
            if (isJsonFileValid(filePath) == false) {
                continue;
            } else {
                var entity = getEntityClassByName(name);
                obj = FileService.generateObjectFromJsonFile(file, entity);
            }

            objectsForImport.put(filePath, obj);
        }

        File[] directories = FileService.getAllDirectories(pathToDirectory);
        for (File directory : directories) {
            prepareObjectsForImport(directory.getAbsolutePath());
        }
    }

    protected void saveObjectsRelations() {
        for (Map.Entry<String, Object> entry : objectsForImport.entrySet()) {
            String filePath = entry.getKey();
            Object obj = entry.getValue();

            if (filePath.contains("page")) {
            	// we are in page folder
                var pageFolder = new File(filePath).getParentFile();
                var chapterFolder = pageFolder.getParentFile();
                var storylineFolder = chapterFolder.getParentFile();
                if (obj instanceof Component) {
                    var page = (Page) getImportObjectByKey(pageFolder.getAbsolutePath() + File.separator + "page.json");
                    var component = (Component) obj;
                    component.setPage(page);
                    saveObject(component, filePath);
                } else if (obj instanceof NotebookEntry) {
                    var page = (Page) getImportObjectByKey(pageFolder.getAbsolutePath() + File.separator + "page.json");
                    var notebookEntry = (NotebookEntry) obj;
                    notebookEntry.setPage(page);
                    saveObject(notebookEntry, filePath);
                } else if (obj instanceof Page) {
                    var page = (Page) obj;
                    var chapter = (Chapter) getImportObjectByKey(chapterFolder.getAbsolutePath() + File.separator + "chapter.json");
                    var storyline = (Storyline) getImportObjectByKey(storylineFolder.getAbsolutePath() + File.separator + "storyline.json");
                    page.setChapter(chapter);
                    page.setStoryline(storyline);
                    saveObject(page, filePath);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	protected Object saveObject(Object obj, String filePath) {
        System.out.println(String.format("Saving: %s", filePath));
        var savedObject = getRepository(obj.getClass()).save(obj);
       // update json file
        FileService.generateJsonFileFromObject(savedObject, filePath);
        return savedObject;
    }

    protected Object getObjectId(Object obj) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Field id;
        if (obj instanceof Component) {
            id = getEntityClassByName("Component").getDeclaredField("id");
        } else if (obj instanceof Resource) {
            id = getEntityClassByName("Resource").getDeclaredField("id");
        } else {
            id = obj.getClass().getDeclaredField("id");
        }

        if (id != null) {
            id.setAccessible(true);
        }

        return id.get(obj);
    }

    protected Class<?> getEntityClassByName(String name) throws ClassNotFoundException {
        if (name.contains("delete")) {
            name = name.replace("delete", "").replaceAll("\\s","");
        }

        Class<?> className = Class.forName("ch.uzh.marugoto.core.data.entity." + StringUtils.capitalize(name));
        return className;
    }


    @SuppressWarnings("rawtypes")
    protected ArangoRepository getRepository(Class clazz) {
        ArangoRepository repository;
        String[] componentsName = new String[] {"Exercise", "Component"};
        String resourcesName = "Resource";

        repository = (ArangoRepository) new Repositories(BeanUtil.getContext()).getRepositoryFor(clazz).orElse(null);

        if (repository == null) {
            if (stringContains(clazz.getName(), componentsName)) {
                repository = BeanUtil.getBean(ComponentRepository.class);
            } else if (clazz.getName().contains(resourcesName)) {
                repository = BeanUtil.getBean(ResourceRepository.class);
            }
        }

        return repository;
    }

    public static boolean stringContains(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

    private Object getImportObjectByKey(String key) {
        return objectsForImport.get(key);
    }

    protected boolean isJsonFileValid(String filePath) {
        var valid = true;

        if (filePath.contains("pageTransition")) {
            valid = preparePageTransitionJson(filePath);
        } else if (filePath.contains("topic")) {
            valid = prepareTopicJson(filePath);
        } else if (filePath.contains("dialogResponse")) {
            valid = prepareDialogResponseJson(filePath);
        }

        return valid;
    }

    @SuppressWarnings("unchecked")
	private boolean prepareTopicJson(String filePath) {
        var file = new File(filePath);
        var valid = true;

        try {
            JsonNode jsonNodeRoot = mapper.readTree(file);
            var startPage = jsonNodeRoot.get("startPage");

            if (startPage.isTextual()) {
                var page = getRepository(Page.class).findById(startPage.asText()).orElse(null);
                if (page == null) {
                    valid = false;
                    System.out.println(String.format("Wrong page id provided: %s", filePath));
                } else {
                    ((ObjectNode)jsonNodeRoot).replace("startPage", mapper.convertValue(page, JsonNode.class));
                    FileService.generateJsonFileFromObject(jsonNodeRoot, filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return valid;
    }

    @SuppressWarnings("unchecked")
	protected boolean preparePageTransitionJson(String filePath) {
        var file = new File(filePath);
        var valid = true;
        JsonNode jsonNodeRoot;

        try {
            jsonNodeRoot = mapper.readTree(file);
            var to = jsonNodeRoot.get("to");
            valid = !to.isNull();

            if (to.isTextual()) {
                String toPageId = to.asText();
                var page = getRepository(Page.class).findById(toPageId).orElse(null);
                ((ObjectNode)jsonNodeRoot).replace("to", mapper.convertValue(page, JsonNode.class));
            }

            if (valid) {
                var from = jsonNodeRoot.get("from");
                Object fromPage = null;
                if (from.isNull()) {
                    var fromPageJson = new File(file.getParentFile().getAbsolutePath() + File.separator + "page.json");
                    fromPage = FileService.generateObjectFromJsonFile(fromPageJson, Page.class);
                    ((ObjectNode)jsonNodeRoot).replace("from", mapper.convertValue(fromPage, JsonNode.class));
                } else if (from.isTextual()) {
                    fromPage = getRepository(Page.class).findById(from.asText()).orElse(null);
                }

                ((ObjectNode)jsonNodeRoot).replace("from", mapper.convertValue(fromPage, JsonNode.class));

                FileService.generateJsonFileFromObject(jsonNodeRoot, filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return valid;
    }

    protected boolean prepareDialogResponseJson(String filePath) {
        var file = new File(filePath);
        var valid = true;
        JsonNode jsonNodeRoot;

        try {
            jsonNodeRoot = mapper.readTree(file);
            var from = jsonNodeRoot.get("from");
            var to = jsonNodeRoot.get("to");
            var pageTransition = jsonNodeRoot.get("pageTransition");
            valid = !from.isNull() && !to.isNull();

            if (valid && from.isTextual()) {
                var dialogSpeech = getRepository(DialogSpeech.class).findById(from.asText()).orElse(null);
                ((ObjectNode)jsonNodeRoot).replace("from", mapper.convertValue(dialogSpeech, JsonNode.class));
            }

            if (valid && to.isTextual()) {
                var dialogSpeech = getRepository(DialogSpeech.class).findById(to.asText()).orElse(null);
                ((ObjectNode)jsonNodeRoot).replace("to", mapper.convertValue(dialogSpeech, JsonNode.class));
            }

            if (valid && !pageTransition.isTextual()) {
                var transition = getRepository(PageTransition.class).findById(pageTransition.asText()).orElse(null);
                ((ObjectNode)jsonNodeRoot).replace("pageTransition", mapper.convertValue(transition, JsonNode.class));
            }

            FileService.generateJsonFileFromObject(jsonNodeRoot, filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return valid;
    }
}
