package ch.uzh.marugoto.shell.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

import ch.uzh.marugoto.shell.util.Importer;
import ch.uzh.marugoto.shell.util.ImporterFactory;

@ShellComponent
public class DoImportCommand {
	
	@ShellMethod("Updates db from folder structure, path to the {generated} folder should be provided with import mode {insert/update/override}")
	public void doImportStep(String pathToDirectory, String importMode) throws ImporterFactory.ImporterNotFoundException {

		Importer importer = ImporterFactory.getImporter(pathToDirectory, importMode);

		System.out.println(String.format(StringUtils.capitalize(importMode) + " started..."));

		importer.doImport();

		System.out.println(String.format("Finished"));
	}
}
