package ch.uzh.marugoto.shell.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class EchoCommand {

	@ShellMethod("Prints the entered input string back into the terminal.")
	public void echo(@ShellOption(help = "Any text string you like") String input) {
		System.out.println("You entered: " + input);
	}
}