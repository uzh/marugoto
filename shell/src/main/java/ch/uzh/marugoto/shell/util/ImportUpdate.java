package ch.uzh.marugoto.shell.util;

import java.io.File;

public class ImportUpdate extends BaseImport implements Importer {

    public ImportUpdate(String pathToFolder) {
        super(pathToFolder);
    }

    @Override
    public void doImport() {
        importFiles(this);
    }

    @Override
    public void filePropertyCheck(File jsonFile, String key) throws Exception {
        var jsonNode = mapper.readTree(jsonFile);
        if ((key).equals("id") && jsonNode.get(key).isNull()) {
            throw new Exception(String.format("Missing ID value in file `%s`.", jsonFile));
        }
    }

    @Override
    public void afterImport(File jsonFile) {
        System.out.println("Updated: " + jsonFile.getAbsolutePath());
    }

    @Override
    public void referenceFileFound(File jsonFile, String key, File referenceFile) {}
}
