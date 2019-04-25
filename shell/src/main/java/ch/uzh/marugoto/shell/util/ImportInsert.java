package ch.uzh.marugoto.shell.util;

import java.io.File;
import java.util.ArrayList;

public class ImportInsert extends BaseImport implements Importer {

    private ArrayList<Object> savedObjects = new ArrayList<>();

    public ImportInsert(String path,String importerId) throws Exception {
        super(path,importerId);
        prepareObjectsForImport(getFolderPath(path,importerId));
    }

    @Override
    public void doImport() {
        importFiles(this);
    }

    @Override
    public void filePropertyCheck(File jsonFile, String key) throws Exception {
        var jsonNode = mapper.readTree(jsonFile);

        if (key.equals("id") && jsonNode.get(key).isNull() == false) {
            var object = getObjectsForImport().get(jsonFile.getAbsolutePath());
            if (savedObjects.contains(object) == false) {
                throw new Exception(String.format("File has ID value present `%s`.", jsonFile));
            }
        }
    }

    @Override
    public void afterImport(File jsonFile) {
        System.out.println("Saved :" + jsonFile.getAbsolutePath());
        savedObjects.add(getObjectsForImport().get(jsonFile.getAbsolutePath()));
    }

    @Override
    public void referenceFileFound(File jsonFile, String key, File referenceFile) {
        System.out.println(String.format("Reference found in (%s): %s", jsonFile.getAbsolutePath(), referenceFile.getAbsolutePath()));
    }
}
