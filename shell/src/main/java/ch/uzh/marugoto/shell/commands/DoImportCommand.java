package ch.uzh.marugoto.shell.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import ch.uzh.marugoto.shell.util.Importer;
import ch.uzh.marugoto.shell.util.ImporterFactory;

@ShellComponent
public class DoImportCommand {
	
	@ShellMethod(
		"`/path/to/generated/folder` insert/update/override. Updates db from folder structure"
	)
	public void doImportStep(String pathToDirectory, String importMode) throws ImporterFactory.ImporterNotFoundException {

		Importer importer = ImporterFactory.getImporter(pathToDirectory, importMode);
		System.out.println(String.format(StringUtils.capitalize(importMode) + " started..."));
		importer.doImport();
		System.out.println(String.format("Finished"));
	}
}
