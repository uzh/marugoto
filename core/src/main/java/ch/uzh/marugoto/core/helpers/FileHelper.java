package ch.uzh.marugoto.core.helpers;

import java.io.File;

abstract public class FileHelper {

    /**
     * Generates folder from provided path
     *
     * @param destinationPath
     * @return
     */
    public static File generateFolder(String destinationPath) {
        File folder = new File(destinationPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }

    /**
     * Makes folder with provided name on the provided path
     *
     * @param destinationPath
     * @param folderName
     * @return
     */
    public static File generateFolder(String destinationPath, String folderName) {
        return generateFolder(destinationPath + File.separator + folderName);
    }

    public static void deleteFolder(String pathToDirectory) {
        File folder = new File(pathToDirectory);

        for (var file : folder.listFiles()) {
            file.delete();
        }

        folder.delete();
    }
}
