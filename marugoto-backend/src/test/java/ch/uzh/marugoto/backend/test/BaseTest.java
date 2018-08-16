package ch.uzh.marugoto.backend.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.backend.data.DbConfiguration;

/**
 * Abstract base test class. Each test class should inherit from this one.
 * The tests are executed under the spring profile `testing`, see annotation below.
 * The database is truncated at the start of a test run once.
 * 
 * This base class provides a logger instance, see field {@code Log}.
 * 
 */
@ActiveProfiles("testing")
@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class BaseTest {
    protected final Logger Log = LogManager.getLogger(this.getClass());

	private static boolean _dbInitialized;
	
	
	@Autowired
	private ArangoOperations operations;
	
	@Autowired
	private DbConfiguration _dbConfig;
	
	
	@Before
    public synchronized void beforeTest() {
		if (!_dbInitialized) {
			truncateDatabase();
			_dbInitialized = true;
		}
    }
	
	/**
	 * Drops the testing database and recreates it.
	 */
	protected void truncateDatabase() {
		operations.dropDatabase();
		operations.driver().createDatabase(_dbConfig.database());
		
		Log.info(String.format("Unit-test database `%s` truncated.", _dbConfig.database()));
	}
}
