package ch.uzh.marugoto.backend.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import ch.uzh.marugoto.backend.data.entity.Salutation;
import ch.uzh.marugoto.backend.data.entity.User;
import ch.uzh.marugoto.backend.data.repository.UserRepository;
import ch.uzh.marugoto.backend.test.BaseTest;

import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArangoEntitiesTest extends BaseTest {

	@Autowired
	private UserRepository repository;


	@Test
	public void test1CreateUsers() {
		var entities = Arrays.asList(
				new User(Salutation.Mr, "Fred", "Dark", "fred.dark@test.com"),
				new User(Salutation.Mr, "Peter", "Muller", "peter@muller.ch"),
				new User(Salutation.Mr, "Fred", "Johnson", "fred.johnson@provider.com"),
				new User(Salutation.Mr, "Michelle", "Stark", "michelle.stark@test.com"),
				new User(Salutation.Mr, "Nadja", "Huber", "nadja@huber.co.uk"));
		
		repository.saveAll(entities);

		assertTrue(entities.size() > 0);
	}

	@Test
	public void test2CountUsers() {
		long count = repository.count();
		assertSame(count, 5L);
	}

	@Test
	public void test2LoadUsers() throws Exception {
		var entity = repository.findOne(Example.of(new User(Salutation.Mr, "Fred", "Dark", "fred.dark@test.com")));
		
		assertTrue(entity.isPresent());
		assertEquals("Fred", entity.get().getFirstName());
		assertEquals("Dark", entity.get().getLastName());
		assertEquals("fred.dark@test.com", entity.get().getMail());
		
		assertTrue(entity != null);
	}
}
