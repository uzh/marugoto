package ch.uzh.marugoto.shell.util;

import java.io.File;

public class ImportInsert extends BaseImport implements Importer {
    
	public ImportInsert(String path, String importerId) {
        super(path, importerId);
    }

    @Override
    public void doImport() throws Exception {
	    super.doImport(this, hiddenFolderPath);
    }

    @Override
    public void beforeImport(File jsonFile) {}

    @Override
    public void afterImport(File jsonFile) {
        System.out.println("Saved :" + jsonFile.getAbsolutePath());
    }
}
