package ch.uzh.marugoto.core.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.Classroom;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.resource.RegisterUser;
import ch.uzh.marugoto.core.data.entity.state.GameState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.repository.GameStateRepository;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.ClassroomLinkExpiredException;
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
	@Autowired
	private GameStateRepository gameStateRepository;
	
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
		if (!registeredUser.getPassword().isEmpty()) {
			user.setPasswordHash(passwordEncoder.encode(registeredUser.getPassword()));
		}
		user.setSignedUpAt(LocalDateTime.now());
		saveUser(user);
		return user;
	}

	public void addUserToClassroom(User user, @Nullable String invitationLinkId) throws ClassroomLinkExpiredException {
		if (invitationLinkId != null) {
			Classroom classroom = classroomService.getClassroomByInvitationLink(invitationLinkId);
			classroomService.addUserToClassroom(user, invitationLinkId);
			GameState gameState = user.getCurrentGameState();
			gameState.setClassroom(classroom);
			gameStateRepository.save(gameState);	
		}

		user.setLastLoginAt(LocalDateTime.now());
		saveUser(user);
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
