package ch.uzh.marugoto.backend.test.data;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import ch.uzh.marugoto.backend.data.entity.Salutation;
import ch.uzh.marugoto.backend.data.entity.User;
import ch.uzh.marugoto.backend.data.repository.UserRepository;

import org.junit.runners.MethodSorters;

import org.junit.FixMethodOrder;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("testing")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ArangoEntitiesTest {

	@Autowired
	private UserRepository repository;


	@Test
	public void test1CreateUsers() {
		var entities = Arrays.asList(
				new User(Salutation.Mr, "Ned", "Stark", "ned.stark@test.com"),
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
		assertTrue(count == 5);
	}

//	@Test
//	public void test2LoadUsers() throws Exception {
//		repository.findOne(Example)
//		final Iterable<User> characters = repository.findAll();
//
//		assertTrue(characters != null);
//	}
}
