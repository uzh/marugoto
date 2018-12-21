package ch.uzh.marugoto.shell.commands;

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
import ch.uzh.marugoto.shell.util.BaseImport;

@ShellComponent
public class ImportDbCommand {
    private ArangoOperations operations;
    @Value("${marugoto.database}")
    private String DB_NAME;
    private final String[] EDGE_COLLECTION = new String[] {"pageTransition", "dialogResponse"};
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

        var collectionFolder = new File(pathToCollections);
        insertDocuments(collectionFolder);
        insertEdge(collectionFolder);

        System.out.println("------------------------------------------------");
        System.out.println("                FINISHED                        ");
        System.out.println("------------------------------------------------");
    }

    private void insertDocuments(File collectionFolder) throws IOException, InterruptedException {
        for (File collectionFile : Objects.requireNonNull(collectionFolder.listFiles())) {
            if (BaseImport.stringContains(collectionFile.getName(), EDGE_COLLECTION)) {
                continue;
            }

            insertCollection(collectionFile, "document");
        }
    }

    private void insertEdge(File collectionFolder) throws IOException, InterruptedException {
        for (File collectionFile : Objects.requireNonNull(collectionFolder.listFiles())) {
            if (BaseImport.stringContains(collectionFile.getName(), EDGE_COLLECTION)) {
                insertCollection(collectionFile, "edge");
            }
        }
    }

    /**
     * Inserts collection in DB
     *
     * @param collectionFile
     * @throws IOException
     * @throws InterruptedException
     */
    private void insertCollection(File collectionFile, String type) throws IOException, InterruptedException {
        var filePath = collectionFile.getAbsolutePath();
        var fileName = FileService.getFileNameWithoutExtension(collectionFile);
        var cmd = "arangoimp --file " + filePath + " --server.database dev --server.username root --collection " + fileName + " --on-duplicate ignore --create-collection true --create-collection-type " + type;
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
