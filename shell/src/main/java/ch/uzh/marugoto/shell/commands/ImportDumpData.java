package ch.uzh.marugoto.shell.commands;

import java.io.IOException;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ch.uzh.marugoto.shell.util.FileService;

@ShellComponent
public class ImportDumpData {

	@ShellMethod("`/path/to/folder` Add dump data to database")
	public void importDumpData(String pathToDirectory) throws IOException, InterruptedException {
		 for (var file : FileService.getAllFiles(pathToDirectory)) {
            var name = file.getName();
            var nameWithoutExtension = file.getName().substring(0, file.getName().lastIndexOf('.')); // remove extension
            execCmd(name, nameWithoutExtension,pathToDirectory);
		 }
	}

	@SuppressWarnings("unused")
	private void execCmd(String fileName, String fileNameWithoutExtension, String pathToDirectory) throws InterruptedException, IOException {

		//example command 
		//arangoimp --file "chapter.json" --server.database dev --server.password "" --collection "chapter" --on-duplicate ignore
	
        String cmd = "arangoimp --file "+ fileName +" --server.database dev --server.password \"\\\"\\\"\" --collection "+ fileNameWithoutExtension +"--on-duplicate ignore";
//		Process process = new ProcessBuilder(new String[] {"csh", "-c", cmd})
//				.start();		

//		// Read output
//		StringBuilder out = new StringBuilder();
//		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//		String line = null, previous = null;
//		while ((line = br.readLine()) != null)
//			if (!line.equals(previous)) {
//				previous = line;
//				out.append(line).append('\n');
//				System.out.println(line);
//			}
//
//		// Check result
//		if (process.waitFor() == 0) {
//			System.out.println("Success!");
//			System.exit(0);
//		}
//
//		// ExecutionException
//		System.err.println(cmd);
//		System.err.println(out.toString());
//		System.exit(1);

	}
}
