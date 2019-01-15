package ch.uzh.marugoto.shell.util;

import java.io.File;

public class ImportInsert extends BaseImport implements Importer {

    public ImportInsert(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        truncateDatabase();
        importFiles(this);
    }

    @Override
    public void filePropertyCheck(File jsonFile, String key) throws Exception {
        var jsonNode = mapper.readTree(jsonFile);
        if (key.equals("id") && jsonNode.get(key).isNull() == false) {
            throw new Exception(String.format("File has ID value present `%s`.", jsonFile));
        }
    }

    @Override
    public void afterImport(File jsonFile) {
        System.out.println("Saved :" + jsonFile.getAbsolutePath());
    }

    @Override
    public void referenceFileFound(File jsonFile, String key, File referenceFile) {
        System.out.println(String.format("Reference found: %s", referenceFile.getAbsolutePath()));
    }
}
