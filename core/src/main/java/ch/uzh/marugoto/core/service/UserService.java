package ch.uzh.marugoto.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.dto.RegisterUser;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.UserType;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.DtoToEntityException;
import ch.uzh.marugoto.core.exception.UserNotFoundException;
import ch.uzh.marugoto.core.helpers.DtoHelper;

/**
 * Service for handling user-related tasks like authentication, authorization
 * and registration/sign-up.
 */
@Service
public class UserService implements UserDetailsService {

	@Autowired
	private ClassroomService classroomService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	
	public User getUserByMail(String mail) {
		return userRepository.findByMail(mail);
	}
	
	public User findUserByResetToken(String resetToken) throws UserNotFoundException {
		User user = userRepository.findByResetToken(resetToken);

		if (user == null) {
			throw new UserNotFoundException();
		}

		return user;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var applicationUser = this.getUserByMail(username);
		if (applicationUser == null)
			throw new UsernameNotFoundException(username);

		return new org.springframework.security.core.userdetails.User(applicationUser.getMail(),
				applicationUser.getPasswordHash(), Collections.emptyList());
	}

	/**
	 * Create new user
	 *
	 * @param registeredUser
	 * @return new user
	 * @throws DtoToEntityException
	 */
	public User createUser(RegisterUser registeredUser) throws DtoToEntityException {
		User user = new User();
		DtoHelper.map(registeredUser, user);
		user.setPasswordHash(passwordEncoder.encode(registeredUser.getPassword()));
		saveUser(user);
		return user;
	}

	public void updateAfterAuthentication(User user, @Nullable String invitationLink) {
		if (invitationLink != null) {
			classroomService.addUserToClassroom(user, invitationLink);
		}

		user.setLastLoginAt(LocalDateTime.now());
		saveUser(user);
	}

	/**
	 * Finds all students
	 *
	 * @return explanation List of students
	 */
	public List<User> getStudents() {
		return userRepository.findAllByTypeIsNot(UserType.Supervisor);
	}

	public void updateGameState(User user, GameState gameState) {
		user.setCurrentGameState(gameState);
		saveUser(user);
	}

	public void updatePageState(User user, PageState pageState) {
		user.setCurrentPageState(pageState);
		saveUser(user);
	}

	public String getResetPasswordLink(User user, String passwordResetUrl) {
		user.setResetToken(UUID.randomUUID().toString());
		saveUser(user);
		return passwordResetUrl + "?mail=" + user.getMail() +"&token=" + user.getResetToken();
	}

	public User updatePassword(String resetToken, String newPassword) throws UserNotFoundException {
		User user = findUserByResetToken(resetToken);
		user.setPasswordHash(passwordEncoder.encode(newPassword));
		user.setResetToken(null);
		saveUser(user);
		return user;
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}
}
