package ch.uzh.marugoto.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import ch.uzh.marugoto.core.service.EchoService;

@ShellComponent
public class EchoCommand {

	@Autowired
	private EchoService dummyService;
	
	
	@ShellMethod("Prints the entered input string back into the terminal.")
	public void echo(@ShellOption(help = "Any text string you like") String input) {
		var output = dummyService.echo(input);
		System.out.println(output);
	}
}