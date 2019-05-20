package ch.uzh.marugoto.shell.util;

import java.io.File;

public class ImportUpdate extends BaseImport implements Importer {

    public ImportUpdate(String pathToFolder,String importerId) throws Exception {
        super(pathToFolder,importerId);
        prepareObjectsForImport(getFolderPath(pathToFolder,importerId));
   }

    @Override
    public void doImport() {
        importFiles(this);
    }

    @Override
    public void afterImport(File jsonFile) {
        System.out.println("Updated: " + jsonFile.getAbsolutePath());
    }
}
