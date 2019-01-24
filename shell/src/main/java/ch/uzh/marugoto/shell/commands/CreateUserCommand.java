package ch.uzh.marugoto.shell.commands;

import com.arangodb.springframework.core.ArangoOperations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import ch.uzh.marugoto.core.CoreConfiguration;
import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
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
}