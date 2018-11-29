package ch.uzh.marugoto.shell.util;

public class ImporterFactory {

    private static final String ALLOWED_MODE = "insert,update,override";

    public static Importer getImporter(String pathToFolder, String importMode) throws ImporterNotFoundException {

        switch (importMode) {
            case "insert":
                return new ImportInsert(pathToFolder);
            case "update":
                return new ImportUpdate(pathToFolder);
            case "override":
                return new ImportOverride(pathToFolder);
            default:
                throw new ImporterNotFoundException();
        }
    }

    public static class ImporterNotFoundException extends Throwable {
        public ImporterNotFoundException() {
            super(String.format("Mode not supported. Allowed: " + ALLOWED_MODE));
        }
    }
}
