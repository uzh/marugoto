package ch.uzh.marugoto.backend.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.backend.data.entity.Salutation;
import ch.uzh.marugoto.backend.data.entity.User;
import ch.uzh.marugoto.backend.data.entity.UserType;
import ch.uzh.marugoto.backend.data.repository.UserRepository;
import ch.uzh.marugoto.backend.test.BaseTest;

import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;

/**
 * Simple test cases for User-related entities.
 * 
 * @author Rino
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserEntitiesTest extends BaseTest {

	@Autowired
	private UserRepository userRepository;
	

	@Test
	public void test1CreateUsers() {
		var entities = Arrays.asList(
				new User(Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", UserType.Guest),
				new User(Salutation.Mr, "Peter", "Muller", "peter@muller.ch", UserType.Guest),
				new User(Salutation.Mr, "Fred", "Johnson", "fred.johnson@provider.com", UserType.Guest),
				new User(Salutation.Mr, "Michelle", "Stark", "michelle.stark@test.com", UserType.SwitchAAI),
				new User(Salutation.Mr, "Nadja", "Huber", "nadja@huber.co.uk", UserType.SwitchAAI));
		
		userRepository.saveAll(entities);

		assertTrue(entities.size() > 0);
	}

	@Test
	public void test2CountUsers() {
		long count = userRepository.count();
		assertEquals(count, 5L);
	}

	@Test
	public void test2LoadUsers() throws Exception {
		var entity = userRepository.findOne(Example.of(new User(Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", UserType.Guest)));
		
		assertTrue(entity.isPresent());
		assertEquals("Fred", entity.get().getFirstName());
		assertEquals("Dark", entity.get().getLastName());
		assertEquals("fred.dark@test.com", entity.get().getMail());
		
		assertNotNull(entity);
	}
}
