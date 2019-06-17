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
import ch.uzh.marugoto.shell.util.ImportInsert;
import ch.uzh.marugoto.shell.util.ImportOverride;
import ch.uzh.marugoto.shell.util.ImportUpdate;
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
	private Importer importer;

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

		if (FileHelper.hiddenFolderExist(pathToDirectory, importerId)) {
			var pathToImporterFolder = FileHelper.getPathToImporterFolder(pathToDirectory, importerId);
			var foldersAreTheSame = FileHelper.compareFolders(pathToDirectory, pathToImporterFolder);

			if (foldersAreTheSame) {
				// copy from original folder for file changes
				importer = new ImportUpdate(pathToDirectory, importerId);
			} else {
				System.out.println("WARNING! You are about to remove player state. "
						+ "If you want to procced, please run the command again with the flag ```shell.argument.for.doImport.delete.playerState``` setted to true");
				if (deletePlayerState.toLowerCase().equals(Boolean.TRUE.toString())) {
					System.out.println("deletePlayerState is currently: " + deletePlayerState);
					importer = new ImportOverride(pathToDirectory, importerId);
				}
			}

		} else {
			// generate import hidden folder for the first time
			FileHelper.generateImportFolder(pathToDirectory, importerId);
			importer = new ImportInsert(pathToDirectory, importerId);
		}

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
