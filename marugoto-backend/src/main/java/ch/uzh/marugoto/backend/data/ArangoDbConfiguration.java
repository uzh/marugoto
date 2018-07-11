package ch.uzh.marugoto.backend.data;

import org.springframework.context.annotation.Configuration;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDB.Builder;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.AbstractArangoConfiguration;
 
@Configuration
@EnableArangoRepositories(basePackages = { "ch.uzh.marugoto.backend.data" })
public class ArangoDbConfiguration extends AbstractArangoConfiguration {
	
  @Override
  public Builder arango() {
	  // Reads arangodb.properties file
	  return new ArangoDB.Builder();
  }
 
  @Override
  public String database() {
    return "test";
  }
}