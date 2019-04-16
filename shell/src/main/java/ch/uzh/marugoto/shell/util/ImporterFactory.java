package ch.uzh.marugoto.shell.util;

public class ImporterFactory {

    private static final String ALLOWED_MODE = "insert, update, override";

    public static Importer getImporter(String pathToFolder, String importMode) throws Exception, ImporterNotFoundException {

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
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ImporterNotFoundException() {
            super(String.format("Mode not supported. Allowed: " + ALLOWED_MODE));
        }
    }
}
