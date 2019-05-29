package ch.uzh.marugoto.shell.util;

import java.io.File;

import ch.uzh.marugoto.shell.helpers.FileHelper;

public class ImportUpdate extends BaseImport implements Importer {

    public ImportUpdate(String pathToFolder, String importerId) {
        super(pathToFolder, importerId);
        updatedFolderPath = FileHelper.getPathToImporterFolder(originalFolderPath, importerId.concat("_update"));
        FileHelper.copyFolder(originalFolderPath, updatedFolderPath);
        FileHelper.setRootFolder(updatedFolderPath);
   }

    @Override
    public void doImport() throws Exception {
        super.doImport(this, updatedFolderPath);
        FileHelper.deleteFolder(updatedFolderPath);
    }

    @Override
    public void beforeImport(File jsonFile) {
        var hiddenFilePath = hiddenFolderPath.concat("/" + FileHelper.getRelativeFilePath(new File(updatedFolderPath), jsonFile));
        var idVal = FileHelper.getObjectId(hiddenFilePath);
        FileHelper.updateIdInJsonFile(jsonFile, idVal);
    }

    @Override
    public void afterImport(File jsonFile) {
        var hiddenFilePath = hiddenFolderPath.concat("/" + FileHelper.getRelativeFilePath(new File(updatedFolderPath), jsonFile));
        FileHelper.copyFile(jsonFile.getAbsolutePath(), hiddenFilePath);
        System.out.println("Updated: " + jsonFile.getAbsolutePath());
    }
}
