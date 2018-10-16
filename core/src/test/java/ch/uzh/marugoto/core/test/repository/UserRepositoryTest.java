package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.Salutation;
import ch.uzh.marugoto.core.data.entity.User;
import ch.uzh.marugoto.core.data.entity.UserType;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;

/**
 * Simple test cases for User-related entities.
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTest extends BaseCoreTest {

	@Autowired
	private UserRepository userRepository;
	

	@Test
	public void testCreateMultipleUsers() {
		// Create 5 users
		var users = Arrays.asList(
				new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test"),
				new User(UserType.Guest, Salutation.Mr, "Peter", "Muller", "peter@muller.ch", "test"),
				new User(UserType.Guest, Salutation.Mr, "Fred", "Johnson", "fred.johnson@provider.com", "test"),
				new User(UserType.SwitchAAI, Salutation.Mr, "Nadja", "Huber", "nadja@huber.co.uk", "test"));
		
		userRepository.saveAll(users);

		assertEquals(4L, users.size());
		assertEquals(5L, userRepository.count());
	}

	@Test
	public void testLoadUser() throws Exception {
		// Create user to load
		userRepository.save(new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test"));
		
		// Load user
		var user = userRepository.findOne(Example.of(new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test")));

		assertNotNull(user);
		assertTrue(user.isPresent());
		assertEquals("Fred", user.get().getFirstName());
		assertEquals("Dark", user.get().getLastName());
		assertEquals("fred.dark@test.com", user.get().getMail());
		assertEquals("test", user.get().getPasswordHash());
	}

	@Test
	public void findByMail() {
		assertNotNull(userRepository.findByMail("unittest@marugoto.ch"));
	}
	
	@Test
	public void testFindByResetToken () {
	
		User user = new User();
		user.setResetToken(UUID.randomUUID().toString());
		userRepository.save(user);
		assertNotNull(userRepository.findByResetToken(user.getResetToken()));
	}	
	
	
}