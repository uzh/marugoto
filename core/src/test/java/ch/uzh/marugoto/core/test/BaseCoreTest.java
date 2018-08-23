package ch.uzh.marugoto.core.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.core.data.DbConfiguration;

/**
 * Abstract base test class. Each test class should inherit from this one.
 * The tests are executed under the spring profile `testing`, see annotation below.
 * The database is truncated at the start of a test run once.
 * 
 * This base class provides a logger instance, see field {@code Log}.
 * 
 */
@ActiveProfiles("testing")
@SpringBootTest(classes={CoreTestApplication.class})
@RunWith(SpringRunner.class)
public abstract class BaseCoreTest {
    protected final Logger Log = LogManager.getLogger(this.getClass());

	private boolean dbInitialized;
	
	
	@Autowired
	private ArangoOperations operations;

	@Autowired
	private DbConfiguration dbConfig;
	
	
	@Before
    public synchronized void before() {
		if (!dbInitialized) {
			setupOnce();
			dbInitialized = true;
		}
    }
	
	/**
	 * Method which is called once for each test class.
	 * By default, it truncates the unit-test database.
	 * Override this method to initialize database with default entities
	 * used for unit tests.
	 */
	protected void setupOnce() {
		truncateDatabase();
	}
	
	/**
	 * Truncates the unit-test database.
	 */
	protected void truncateDatabase() {
		operations.dropDatabase();
		operations.driver().createDatabase(dbConfig.database());
		
		Log.info("Unit-test database `{}` truncated.", dbConfig.database());
	}
}