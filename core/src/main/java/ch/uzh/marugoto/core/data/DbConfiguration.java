package ch.uzh.marugoto.core.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;

/**
 * Configuration for ArangoDB access. The connection settings are stored in the
 * application(-XXX).properties files.
 */
@Configuration
@EnableArangoRepositories(basePackages = { "ch.uzh.marugoto" })
public class DbConfiguration extends AbstractArangoConfiguration {

	/**
	 * Reads database name from application.properties file.
	 */
	@Value("${marugoto.database}")
	private String dbName;
	
	/**
	 * Reads host name from application.properties file.
	 */
	@Value("${arangodb.host}")
	private String arangoDbHost;
	
	/**
	 * Reads port from application.properties file.
	 */
	@Value("${arangodb.port}")
	private int arangoDbPort;
	
	/**
	 * Reads user from application.properties file.
	 */
	@Value("${arangodb.user}")
	private String arangoDbUser;
	
	/**
	 * Reads password from application.properties file.
	 */
	@Value("${arangodb.password}")
	private String arangoDbPassword;

	@Override
	public Builder arango() {
		// Reads application.properties file according to active profile (default, testing)
		return new Builder().host(arangoDbHost, arangoDbPort).user(arangoDbUser).password(arangoDbPassword);
	}

	@Override
	public String database() {
		return dbName;
	}
}