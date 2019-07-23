package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ch.uzh.marugoto.core.data.entity.application.Gender;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.exception.ClassroomLinkExpiredException;
import ch.uzh.marugoto.core.service.UserService;
import ch.uzh.marugoto.core.test.BaseCoreTest;

/**
 * Simple tests for the UserService class
 *
 */
public class UserServiceTest extends BaseCoreTest {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService; 
	private User user;
	private User shibbolethUser;

	@Before
	public synchronized void before() {
		super.before();
		user = userRepository.findByMail("unittest@marugoto.ch");
		shibbolethUser = userRepository.findByMail("donald@marugoto.ch");
	}

	@Test
	public void testGetUserByEmail () {
		var testUser = userService.getUserByMail(user.getMail());
		assertEquals(user.getFirstName(), testUser.getFirstName());
		assertEquals(user.getGender(), testUser.getGender());
	}
	
	@Test
	public void testFindUserByResetToken () throws Exception {
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		var userWithToken = userService.findUserByResetToken(user.getResetToken());
		assertNotNull(userWithToken);
		assertEquals (user.getMail(),userWithToken.getMail()); 
	}	
	
	@Test
	public void testLoadUserByUserName () {
		var testUser = userService.loadUserByUsername(user.getMail());
		assertNotNull(testUser);
		assertTrue(testUser.isEnabled());
		assertTrue(testUser.isCredentialsNonExpired());
	}

	@Test
	public void testLoadShibbolethUserByUserName () {
		var testUser = userService.loadUserByUsername(shibbolethUser.getMail());
		assertNotNull(testUser);
		assertTrue(testUser.isEnabled());
		assertTrue(testUser.isCredentialsNonExpired());
	}
	
	@Test
	public void testUpdateAfterAuthentication () throws ClassroomLinkExpiredException {
		userService.addUserToClassroom(user, null);
		assertNotNull(user.getLastLoginAt());
	}
	
	@Test
	public void testSaveUser () {
		var user = new User(Gender.Male, "New", "User", "createuser@marugoto.ch", "test");
		userService.saveUser(user);
	}
}
