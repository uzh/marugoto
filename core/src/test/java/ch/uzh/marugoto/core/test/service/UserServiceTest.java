package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
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
	
		
	@Test
	public void testGetUserByEmail() {
		
		var users = Lists.newArrayList(userRepository.findAll(new Sort(Direction.ASC, "firstName")));
		var user1Email = users.get(0).getMail();
		
		User user = userService.getUserByMail(user1Email);
		
		assertEquals("Fredi", user.getFirstName());
		assertEquals(Salutation.Mr, user.getSalutation());
	}

}
