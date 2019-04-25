package ch.uzh.marugoto.shell.util;

import java.io.File;

public interface Importer {
    void doImport() throws Exception;
    void filePropertyCheck(File jsonFile, String key) throws Exception;
    void afterImport(File jsonFile);
    void referenceFileFound(File jsonFile, String key, File referenceFile);
}
