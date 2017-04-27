package computer.lanoel.platform.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by amcde on 3/23/2017.
 */
public class DatabaseUpgrader {

    private Connection _conn;
    private boolean _isFailed = false;

    public DatabaseUpgrader(Connection conn) { _conn = conn; }


    public boolean upgradeDatabase(String sqlFileDirectory) throws Exception
    {
        createUpgradeTableIfNotExists();
        try(Stream<Path> pathStream = Files.walk(Paths.get(sqlFileDirectory)).filter(p -> p.getFileName().toString().endsWith(".sql"))) {

            pathStream.forEach(p -> {
                try {
                    applyUpgradeFromFile(p);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    _isFailed = true;
                }
            });

            return !_isFailed;

        } finally {
            try {
                if (_conn != null) {
                    _conn.close();
                }
            } catch (SQLException ex) {
                System.out.println("error caught: " + ex.getMessage());
            }
        }
    }

    private void applyUpgradeFromFile(Path path) throws Exception
    {
        String fileName = path.getFileName().toString();
        if(hasSqlFileBeenApplied(fileName))
        {
            System.out.println("SQL file has already been run: " + fileName);
            return;
        }
        String allLines = Files.readAllLines(path).stream().filter(l -> !l.startsWith("--")).collect(Collectors.joining());
        String[] sqls = allLines.split(";");
        String currentSql = null;

        try
        {
            for(String sql : sqls)
            {
                currentSql = sql;
                System.out.println("Running SQL:\n" + currentSql);
                PreparedStatement ps = _conn.prepareStatement(currentSql);
                ps.execute();
            }
            currentSql = null;
            recordCompletedSqlFile(fileName);
            _conn.commit();
        } catch (Exception e)
        {
            if(currentSql != null)
            {
                System.out.println("Failed on :\n" + currentSql);
            }
            System.out.println("error caught: " + e.getMessage());
            throw e;
        } finally
        {
            _conn.rollback();
        }
    }

    private boolean hasSqlFileBeenApplied(String fileName) throws Exception
    {
        String getSql = "SELECT * FROM completed_upgrade_sql_files WHERE FileName=?";
        PreparedStatement ps = _conn.prepareStatement(getSql);
        ps.setString(1, fileName);
        ResultSet rs = ps.executeQuery();

        if(rs.isBeforeFirst())
        {
            return true;
        }

        return false;
    }

    private void recordCompletedSqlFile(String fileName) throws Exception
    {
        System.out.println("Recording successfully processed sql file: " + fileName);
        String recordSql = "INSERT INTO completed_upgrade_sql_files (FileName) VALUES (?);";
        PreparedStatement ps = _conn.prepareStatement(recordSql);
        ps.setString(1, fileName);
        ps.execute();
        // commit outside this method
    }

    private void createUpgradeTableIfNotExists() throws Exception
    {
        String upgradeTableSql = "" +
                "CREATE TABLE IF NOT EXISTS completed_upgrade_sql_files" +
                " (" +
                " CompletedFilesKey BIGINT PRIMARY KEY AUTO_INCREMENT," +
                " FileName VARCHAR(2000) NOT NULL," +
                " DateApplied DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";
        PreparedStatement ps = _conn.prepareStatement(upgradeTableSql);
        ps.executeUpdate();
        _conn.commit();
    }
}
