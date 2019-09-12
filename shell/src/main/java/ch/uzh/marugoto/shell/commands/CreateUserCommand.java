package ch.uzh.marugoto.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.repository.UserRepository;

@ShellComponent
public class CreateUserCommand {

	@Autowired
	private ArangoOperations operations;
	@Autowired
	private CoreConfiguration coreConfig;
	@Autowired
	private UserRepository userRepository;

	@ShellMethod("`firstname lastname mail password`. Used for adding user to database.")
	public void createUser(String firstname, String lastname, String mail, String password) {
		System.out.println("Creating user ...");

		operations.collection("user");

		if (userRepository.findByMail(mail) != null) {
			System.out.println(String.format("User already exist in DB `%s`. Skipping", mail));
		} else {
			var user1 = new User(firstname, lastname, mail, coreConfig.passwordEncoder().encode(password));
			userRepository.save(user1);

			System.out.println(String.format("User has been added to database: mail: %s ; pass: %s", mail, password));
		}
	}
}