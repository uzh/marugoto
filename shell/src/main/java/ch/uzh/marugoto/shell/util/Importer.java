package ch.uzh.marugoto.shell.util;

import java.io.File;

public interface Importer {
    void doImport() throws Exception;
    void beforeImport(File jsonFile);
    void afterImport(File jsonFile);
}
