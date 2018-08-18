package ch.uzh.marugoto.backend.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import com.google.common.collect.Lists;

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
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRepositoryTest extends BaseTest {

	@Autowired
	private UserRepository userRepository;
	

	@Test
	public void test1CreateUsers() {
		var entities = Arrays.asList(
				new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test"),
				new User(UserType.Guest, Salutation.Mr, "Peter", "Muller", "peter@muller.ch", "test"),
				new User(UserType.Guest, Salutation.Mr, "Fred", "Johnson", "fred.johnson@provider.com", "test"),
				new User(UserType.SwitchAAI, Salutation.Mr, "Michelle", "Stark", "michelle.stark@test.com", "test"),
				new User(UserType.SwitchAAI, Salutation.Mr, "Nadja", "Huber", "nadja@huber.co.uk", "test"));
		
		userRepository.saveAll(entities);

		assertTrue(entities.size() > 0);
	}

	@Test
	public void test2CountUsers() {
		var users = Lists.newArrayList(userRepository.findAll());
		long count = userRepository.count();
		assertEquals(5L, count);
	}

	@Test
	public void test2LoadUsers() throws Exception {
		var entity = userRepository.findOne(Example.of(new User(UserType.Guest, Salutation.Mr, "Fred", "Dark", "fred.dark@test.com", "test")));
		
		assertTrue(entity.isPresent());
		assertEquals("Fred", entity.get().getFirstName());
		assertEquals("Dark", entity.get().getLastName());
		assertEquals("fred.dark@test.com", entity.get().getMail());
		assertEquals("test", entity.get().getPasswordHash());
		
		assertNotNull(entity);
	}
}