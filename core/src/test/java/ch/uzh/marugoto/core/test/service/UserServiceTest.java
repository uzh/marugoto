package ch.uzh.marugoto.core.test.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.collect.Lists;

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
		
		var users = Lists.newArrayList(userRepository.findAll(new Sort(Direction.ASC, "firstName")));
		var user1Email = users.get(0).getMail();
		
		User user = userRepository.findByMail(user1Email);
		
		assertEquals("Fredi", user.getFirstName());
		assertEquals(Salutation.Mr, user.getSalutation());
	}

}
