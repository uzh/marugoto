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

@ActiveProfiles("testing")
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class BaseTest {
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

	private static boolean _dbInitialized;
	
	
	@Autowired
	private ArangoOperations operations;
	
	@Autowired
	private DbConfiguration _dbConfig;
	
	
	@Before
    public synchronized void beforeTest() {
		if (!_dbInitialized) {
			// Make sure to recreate database only once when running unit tests
			truncateDatabase();
			_dbInitialized = true;
		}
    }
	
	/**
	 * Drops the testing database and recreates it.
	 */
	protected void truncateDatabase() {
		// Drop testing database and recreate it
		operations.dropDatabase();
		operations.driver().createDatabase(_dbConfig.database());
		
		logger.info(String.format("Unit-test database `%s` truncated.", _dbConfig.database()));
	}
}
