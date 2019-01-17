package ch.uzh.marugoto.shell.commands;

import com.arangodb.springframework.core.ArangoOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import ch.uzh.marugoto.shell.util.Importer;
import ch.uzh.marugoto.shell.util.ImporterFactory;

@ShellComponent
public class DoImportCommand {

	@Value("${marugoto.database}")
	private String DB_NAME;
	@Value("${spring.profiles.active}")
	private String SPRING_PROFILE;
	@Autowired
	private ArangoOperations operations;

	@ShellMethod(
		"`/path/to/generated/folder` insert/update/override. Updates db from folder structure"
	)
	public void doImport(String pathToDirectory, String importMode) throws ImporterFactory.ImporterNotFoundException {
		System.out.println("Preparing database:  " + DB_NAME);
		prepareDb();

		System.out.println(StringUtils.capitalize(importMode) + " started...");

		Importer importer = ImporterFactory.getImporter(pathToDirectory, importMode);
		importer.doImport();
		// we need this for states collections
		createMissingCollections();

		System.out.println("Finished");
	}

	private void prepareDb() {
		// Make sure database exists, create if not
		if (!operations.driver().getDatabases().contains(DB_NAME)) {
			operations.driver().createDatabase(DB_NAME);
		}
	}

	private void createMissingCollections() {
		//check if every collection is added
		operations.collection("chapter");
		operations.collection("character");
		operations.collection("component");
		operations.collection("dialogResponse");
		operations.collection("dialogSpeech");
		operations.collection("exerciseState");
		operations.collection("notebookEntry");
		operations.collection("page");
		operations.collection("pageState");
		operations.collection("pageTransition");
		operations.collection("personalNote");
		operations.collection("resource");
		operations.collection("storyline");
		operations.collection("storylineState");
		operations.collection("topic");
		operations.collection("user");
	}
}
