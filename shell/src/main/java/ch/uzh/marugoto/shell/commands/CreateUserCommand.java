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
	public void createUser(String firstname, String lastname, String mail, String password) throws Exception {
		System.out.println("Creating user ...");

		operations.collection("user");

		if (userRepository.findByMail(mail) != null) {
			throw new Exception(String.format("User already exist in DB `%s`", mail));
		}

		var user1 = new User(UserType.Guest, Salutation.Mr, firstname, lastname, mail, coreConfig.passwordEncoder().encode(password));
		userRepository.save(user1);

		System.out.println(String.format("User `%s` written to database. Finished.", mail));
	}

	@ShellMethod("`firstname lastname mail password`. Used for adding supervisor to database.")
	public void createSupervisor(String firstname, String lastname, String mail, String password) throws Exception {
		System.out.println("Creating supervisor ...");

		if (userRepository.findByMail(mail) != null) {
			throw new Exception(String.format("Supervisor already exist in DB `%s`", mail));
		}

		var user1 = new User(UserType.Supervisor, Salutation.Mr, firstname, lastname, mail, coreConfig.passwordEncoder().encode(password));
		userRepository.save(user1);

		System.out.println(String.format("Supervisor `%s` written to database. Finished.", mail));
	}

	@ShellMethod("Used for adding supervisor and user to database for development. This should be removed in production")
	public void createDevUsers() {
		var userMail = "dev@marugoto.dev";
		var supervisorMail = "supervisor@marugoto.dev";

		var user = userRepository.findByMail(userMail);
		if (user == null) {
			userRepository.save(
					new User(UserType.Guest,
							Salutation.Mr,
							"User",
							"Dev-Marugoto",
							userMail,
							coreConfig.passwordEncoder().encode("dev")));
		}

		var supervisor = userRepository.findByMail(supervisorMail);
		if (supervisor == null) {
			userRepository.save(
					new User(UserType.Guest,
							Salutation.Mr,
							"Supervisor",
							"Dev-Marugoto",
							supervisorMail,
							coreConfig.passwordEncoder().encode("dev")));
		}
	}
}