package ch.uzh.marugoto.core.helpers;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

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

    /**
     * Deletes folder
     *
     * @param pathToDirectory
     */
    public static void deleteFolder(String pathToDirectory) {
        File folder = new File(pathToDirectory);
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException e) {
            throw new RuntimeException("Delete folder error: " + e.getMessage());
        }
//        for (var file : folder.listFiles()) {
//            file.delete();
//        }
//
//        folder.delete();
    }
    
	/**
	 * Returns all files that are not hidden from folder
	 *
	 * @param pathToDirectory
	 * @return files
	 */
	public static File[] getAllFiles(String pathToDirectory) {
		File folder = new File(pathToDirectory);
		return folder.listFiles(file -> !file.isHidden() && !file.isDirectory());
	}

}
