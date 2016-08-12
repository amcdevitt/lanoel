package computer.lanoel.platform.database;

import java.sql.Connection;

public interface IDatabase {

	void UpgradeStorage() throws Exception;
	boolean storageAvailable();
	void setConnection(Connection conn);
}
