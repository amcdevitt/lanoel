/**
 * 
 */
package test.omegasixcloud.catalogservice.platform;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.omegasixcloud.catalogservice.contracts.TestUtility;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import computer.lanoel.platform.DatabaseManager;
import computer.lanoel.platform.ServiceConstants;

/**
 * @author amcdevitt
 *
 */
public class DatabaseManagerTests {

	private DatabaseManager dbManager;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception 
	{
		dbManager = new DatabaseManager(ServiceConstants.testUrl, 
				ServiceConstants.testUsername, ServiceConstants.testPassword);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void printResource() throws Exception {
		URL url = Resources.getResource("database/mysql/00_createCatalogItemTable.sql");
		assertNotNull(url);
		String res = Resources.toString(url, Charsets.UTF_8);
		System.out.println(res);
	}
	
	/**
	 * Ensure we don't have DELETES in upgrade SQLs
	 * @throws Exception
	 */
	@Test
	public void database_scripts_NoDeletes() throws Exception 
	{
		List<String> resources = DatabaseManagerTestData.getDatabaseResources();
		
		for(String res : resources)
		{
			assertFalse(res.toLowerCase().contains("delete"));
		}
	}
	
	/**
	 * Ensure we don't have DROP in upgrade SQLs
	 * @throws Exception
	 */
	@Test
	public void database_scripts_NoDrop() throws Exception 
	{
		List<String> resources = DatabaseManagerTestData.getDatabaseResources();
		
		for(String res : resources)
		{
			assertFalse(res.toLowerCase().contains("drop table"));
		}
	}
}
