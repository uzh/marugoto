package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

public class UserServiceTest extends BaseCoreTest {

	
	@Autowired
	private UserRepository userRepository;
	
		
	@Test
	public void testGetUserByEmail() {
		var newUser = new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test");
		userRepository.save(newUser);
		User user = userRepository.findByMail("fred.dark@test.com");
		assertEquals("Fred", user.getFirstName());
		assertEquals(Salutation.Mr, user.getSalutation());
	}
	
}
