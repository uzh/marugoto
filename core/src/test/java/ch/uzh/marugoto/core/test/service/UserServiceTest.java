package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Lists;

import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.repository.UserRepository;
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
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();
		
	@Test
	public void testGetUserByEmail() {
		
		var users = Lists.newArrayList(userRepository.findAll(new Sort(Direction.ASC, "firstName")));
		var user1Email = users.get(0).getMail();
		
		User user = userService.getUserByMail(user1Email);
		
		assertEquals("Fredi", user.getFirstName());
		assertEquals(Salutation.Mr, user.getSalutation());
	}
	
	@Test
	public void testLoadUserByUserName () {
		var users = Lists.newArrayList(userRepository.findAll(new Sort(Direction.ASC, "firstName")));
		var userName = users.get(0).getMail();
		var user = userService.loadUserByUsername(userName);
		
		assertNotNull(user);
		assertTrue(user.isEnabled());
		assertTrue(user.isCredentialsNonExpired());
	}
	
	@Test
	public void testValidatePassword() {

		String correctPassword = "Mypassword8";
		String noCapitalLetter = "nocapitalletter";
		String noDigit = "letterWithoutDigit";
		String not8letters = "letter";
		
		boolean correct = userService.validatePassword(correctPassword);
		boolean capital = userService.validatePassword(noCapitalLetter);
		boolean digit = userService.validatePassword(noDigit);
		boolean numberOfletters = userService.validatePassword(not8letters);

		assertTrue(correct);
		assertFalse(capital);
		assertFalse(digit);
		assertFalse(numberOfletters);

	}
	

}
