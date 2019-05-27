package ch.uzh.marugoto.core.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.UUID;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.core.data.entity.application.Gender;
import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.repository.UserRepository;
import ch.uzh.marugoto.core.test.BaseCoreTest;

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
		var currentSize = userRepository.count();
		// Create 5 users
		var users = Arrays.asList(
				new User(Gender.Male, "Fred", "Dark", "fred.dark@test.com", "test"),
				new User(Gender.Male, "Peter", "Muller", "peter@muller.ch", "test"),
				new User(Gender.Male, "Fred", "Johnson", "fred.johnson@provider.com", "test"),
				new User(Gender.Male, "Nadja", "Huber", "nadja@huber.co.uk", "test"));
		
		userRepository.saveAll(users);

		assertEquals(4L, users.size());
		assertEquals(currentSize + users.size(), userRepository.count());
	}

	@Test
	public void testLoadUser() {
		// Create user to load
		userRepository.save(new User(Gender.Male, "Fred", "Dark", "fred.dark@test.com", "test"));
		
		// Load user
		var user = userRepository.findOne(Example.of(new User(Gender.Male, "Fred", "Dark", "fred.dark@test.com", "test")));

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