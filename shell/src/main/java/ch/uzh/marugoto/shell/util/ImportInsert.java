package ch.uzh.marugoto.shell.util;

import java.io.File;

public class ImportInsert extends BaseImport implements Importer {
    
	public ImportInsert(String path, String importerId) throws Exception {
        super(path, importerId);
    }

    @Override
    public void doImport() throws Exception {
    	prepareObjectsForImport(getHiddenFolder());
        importFiles(this);
    }
    
    @Override
    public void afterImport(File jsonFile) {
        System.out.println("Saved :" + jsonFile.getAbsolutePath());
    }

    @Override
    public void referenceFileFound(File jsonFile, String key, File referenceFile) {
        System.out.println(String.format("Reference found in (%s): %s", jsonFile.getAbsolutePath(), referenceFile.getAbsolutePath()));
    }
}
