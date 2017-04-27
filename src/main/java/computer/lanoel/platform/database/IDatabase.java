package computer.lanoel.platform.database;

import java.sql.Connection;

public interface IDatabase {

	boolean storageAvailable();
	void setConnection(Connection conn);
}
