package ch.uzh.marugoto.shell.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.core.data.DbConfiguration;
import ch.uzh.marugoto.shell.util.BeanUtil;

@ShellComponent
public class TruncateDatabaseCommand {
	
	@ShellMethod("truncateDatabase")
	public void truncateDatabase() {
		var dbConfig = BeanUtil.getBean(DbConfiguration.class);
        var operations = BeanUtil.getBean(ArangoOperations.class);

        System.out.println(String.format("Truncating database `%s`...", dbConfig.database()));

        if (operations.driver().getDatabases().contains(dbConfig.database())) {
            operations.dropDatabase();
        }

        operations.driver().createDatabase(dbConfig.database());
	}
}