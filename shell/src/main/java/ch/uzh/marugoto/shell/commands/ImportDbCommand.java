package ch.uzh.marugoto.shell.commands;

import com.arangodb.entity.CollectionType;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.springframework.core.ArangoOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Objects;

import ch.uzh.marugoto.core.service.FileService;

@ShellComponent
public class ImportDbCommand {
    private ArangoOperations operations;
    @Value("${marugoto.database}")
    private String DB_NAME;

    @Autowired
    public ImportDbCommand(ArangoOperations arangoOperations) {
        this.operations = arangoOperations;
    }


    @ShellMethod("Import database from db-dump folder")
    public void importDb(String pathToCollections) throws IOException, InterruptedException {

        System.out.println("------------------------------------------------");
        System.out.println("           IMPORT DB STARTED                    ");
        System.out.println("------------------------------------------------");

        // Make sure database exists, create if not
        if (!operations.driver().getDatabases().contains(DB_NAME)) {
            operations.driver().createDatabase(DB_NAME);
        }

        operations.collection("topic");
        operations.collection("storyline");
        operations.collection("chapter");
        operations.collection("page");
        operations.collection("pageTransition", new CollectionCreateOptions().type(CollectionType.EDGES));
        operations.collection("component");
        operations.collection("notebookEntry");
        operations.collection("personalNote");
        operations.collection("character");
        operations.collection("dialogSpeech");
        operations.collection("dialogResponse", new CollectionCreateOptions().type(CollectionType.EDGES));
        operations.collection("resource");
        operations.collection("user");

        var collectionFolder = new File(pathToCollections);
        for (File collectionFile : Objects.requireNonNull(collectionFolder.listFiles())) {
            insertCollection(collectionFile);
        }

        System.out.println("------------------------------------------------");
        System.out.println("                FINISHED                        ");
        System.out.println("------------------------------------------------");
    }

    /**
     * Inserts collection in DB
     *
     * @param collectionFile
     * @throws IOException
     * @throws InterruptedException
     */
    private void insertCollection(File collectionFile) throws IOException, InterruptedException {
        var filePath = collectionFile.getAbsolutePath();
        var fileName = FileService.getFileNameWithoutExtension(collectionFile);
        var cmd = "arangoimp --file " + filePath + " --server.database dev --server.username root --collection " + fileName + " --on-duplicate ignore";
        // execute shell command
        Process process = Runtime.getRuntime().exec(cmd);
        // answer password prompt
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        bw.write("\n");
        bw.flush();

        System.out.println(String.format("Inserting %s", fileName.toUpperCase()));
        int exitCode = process.waitFor();
        assert exitCode == 0;
    }
}
