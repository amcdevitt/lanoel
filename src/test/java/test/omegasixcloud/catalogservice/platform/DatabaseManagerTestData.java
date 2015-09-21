package test.omegasixcloud.catalogservice.platform;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class DatabaseManagerTestData {

	public static List<String> getDatabaseResources() throws Exception
	{
		List<String> dbResources = new ArrayList<String>();
		
		URL createCatalogItemTableUrl = Resources.getResource("database/mysql/00_createCatalogItemTable.sql");
		String createCatalogItemTableRes = Resources.toString(createCatalogItemTableUrl, Charsets.UTF_8);
		dbResources.add(createCatalogItemTableRes);
		
		return dbResources;
	}
}
