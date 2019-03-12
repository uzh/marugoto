package ch.uzh.marugoto.shell.commands;

import com.arangodb.springframework.core.ArangoOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.topic.Salutation;
import ch.uzh.marugoto.core.data.entity.topic.UserType;
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
			var user1 = new User(UserType.Guest, Salutation.Mr, firstname, lastname, mail, coreConfig.passwordEncoder().encode(password));
			userRepository.save(user1);

			System.out.println(String.format("User has been added to database: mail: %s ; pass: %s", mail, password));
		}
	}

	@ShellMethod("`firstname lastname mail password`. Used for adding supervisor to database.")
	public void createSupervisor(String firstname, String lastname, String mail, String password) {
		System.out.println("Creating supervisor ...");

		if (userRepository.findByMail(mail) != null) {
			System.out.println(String.format("Supervisor already exist in DB `%s`. Skipping", mail));
		} else {
			var user1 = new User(UserType.Supervisor, Salutation.Mr, firstname, lastname, mail, coreConfig.passwordEncoder().encode(password));
			userRepository.save(user1);

			System.out.println(String.format("Supervisor has been added to database: mail: %s ; pass: %s", mail, password));
		}
	}

	@ShellMethod("Used for adding supervisor and user to database for development. This should be removed in production")
	public void createDevUsers() {
		createUser("Rocky", "Balboa", "dev@marugoto.dev", "dev");
		createSupervisor("Mickey", "Goldmill", "supervisor@marugoto.dev", "dev");
	}
}