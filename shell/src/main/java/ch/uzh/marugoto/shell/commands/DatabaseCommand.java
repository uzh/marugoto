package ch.uzh.marugoto.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.arangodb.entity.CollectionType;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.shell.helpers.FileHelper;
import ch.uzh.marugoto.shell.util.BeanUtil;
import ch.uzh.marugoto.shell.util.Importer;

@ShellComponent
public class DatabaseCommand {

	@Value("${marugoto.database}")
	private String DB_NAME;
	@Value("${spring.profiles.active}")
	private String SPRING_PROFILE;
	@Value("${shell.argument.for.doImport.path}")
	private String shellArgumentForDoImportPath;
	@Value("${shell.argument.for.doImport.importerId}")
	private String shellArgumentForDoImportImporterId;
	@Value("${shell.argument.for.doImport.delete.playerState}")
	private String shellArgumentForDoImportDeletePlayerState;
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

	@ShellMethod("`/path/to/generated/folder` importerFolderName. boolean for deleting player state")
	public void doImport(String pathToDirectory, String importerId,
			@ShellOption(defaultValue = "false") String deletePlayerState) throws Exception {
		// we need this for states collections
		createMissingCollections();

		System.out.println("Preparing database:  " + DB_NAME);
		prepareDb();

		if (System.getProperty("os.name").compareTo("Windows 7") == 0) {
			pathToDirectory = pathToDirectory.replace("/", "\\");
		}

		boolean deletePlayerStates = deletePlayerState.toLowerCase().equals(Boolean.TRUE.toString());

		Importer importer = new Importer(pathToDirectory, importerId);
		try {
			if (FileHelper.hiddenFolderExist(pathToDirectory, importerId) && deletePlayerStates == true) {
				importer.removePlayerStates();
				importer.deleteTopic();
			} else if (FileHelper.hiddenFolderExist(pathToDirectory, importerId) && deletePlayerStates == false) {
				importer.deactivateTopic();
			}
		} catch(Exception e) {
			// keep importer running!!!
		}
		importer.deleteHiddenFolder();

		FileHelper.generateImportFolder(pathToDirectory, importerId);
		importer.doImport();
		System.out.println("Finished");
	}

	@ShellMethod("Create missing collections")
	public void createMissingCollections() {
		// check if every collection is added
		operations.collection("chapter");
		operations.collection("character");
		operations.collection("component");
		operations.collection("notification");
		operations.collection("mailState");
		operations.collection("mailReply");
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

	/**
	 * Called on shell container startup
	 * 
	 * @param event
	 * @throws ImporterNotFoundException
	 * @throws Exception
	 */
	@EventListener(ContextRefreshedEvent.class)
	public void contextRefreshedEvent(ContextRefreshedEvent event) throws Exception {
		if (!shellArgumentForDoImportPath.isEmpty()) {
			System.out.println("doImport with path: " + shellArgumentForDoImportPath);
			
			try {
				doImport(shellArgumentForDoImportPath, shellArgumentForDoImportImporterId,
						shellArgumentForDoImportDeletePlayerState);
			} catch (Exception e) {
				System.exit(1);
			}
			System.exit(0);
		}

	}
}
