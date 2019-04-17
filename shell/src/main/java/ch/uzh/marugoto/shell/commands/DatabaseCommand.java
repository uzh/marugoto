package ch.uzh.marugoto.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import com.arangodb.entity.CollectionType;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.shell.util.BeanUtil;
import ch.uzh.marugoto.shell.util.Importer;
import ch.uzh.marugoto.shell.util.ImporterFactory;

@ShellComponent
public class DatabaseCommand {


    @Value("${marugoto.database}")
    private String DB_NAME;
    @Value("${spring.profiles.active}")
    private String SPRING_PROFILE;
    @Autowired
    private ArangoOperations operations;


    @ShellMethod("Truncate Database")
    public void truncateDatabase() {
        var dbConfig = BeanUtil.getBean(DbConfiguration.class);
        var operations = BeanUtil.getBean(ArangoOperations.class);

        System.out.println(String.format("Truncating database `%s`...", dbConfig.database()));

        if (operations.driver().getDatabases().contains(dbConfig.database())) {
            operations.dropDatabase();
        }

        operations.driver().createDatabase(dbConfig.database());
    }

    @ShellMethod("`/path/to/generated/folder` insert/update/override. Updates db from folder structure")
    public void doImport(String pathToDirectory, String importMode) throws ImporterFactory.ImporterNotFoundException {
        System.out.println("Preparing database:  " + DB_NAME);
        prepareDb();

        System.out.println(StringUtils.capitalize(importMode) + " started...");

        if(System.getProperty("os.name").compareTo("Windows 7") == 0) {
            pathToDirectory = pathToDirectory.replace("/", "\\");
        }

        Importer importer = ImporterFactory.getImporter(pathToDirectory, importMode);
        importer.doImport();
        // we need this for states collections
        createMissingCollections();

        System.out.println("Finished");
    }

    @ShellMethod("Create missing collections")
    public void createMissingCollections() {
        //check if every collection is added
        operations.collection("chapter");
        operations.collection("character");
        operations.collection("component");
        operations.collection("notification");
        operations.collection("mailState");
        operations.collection("dialogState");
        operations.collection("dialogResponse", new CollectionCreateOptions().type(CollectionType.EDGES));
        operations.collection("dialogSpeech");
        operations.collection("exerciseState");
        operations.collection("notebookEntry");
        operations.collection("notebookEntryState");
        operations.collection("notebookContent");
        operations.collection("page");
        operations.collection("pageState");
        operations.collection("pageTransition", new CollectionCreateOptions().type(CollectionType.EDGES));
        operations.collection("personalNote");
        operations.collection("resource");
        operations.collection("gameState");
        operations.collection("topic");
        operations.collection("user");
        operations.collection("classroom");
        operations.collection("classroomMember", new CollectionCreateOptions().type(CollectionType.EDGES));
    }
    
    private void prepareDb() {
        // Make sure database exists, create if not
        if (!operations.driver().getDatabases().contains(DB_NAME)) {
            operations.driver().createDatabase(DB_NAME);
        }
    }
}