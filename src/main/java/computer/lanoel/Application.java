package computer.lanoel;


import com.google.common.io.Resources;
import computer.lanoel.platform.ServiceUtils;
import computer.lanoel.platform.database.DatabaseUpgrader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import computer.lanoel.platform.InitialPersonInfo;
import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.platform.database.DatabaseFactory;
import computer.lanoel.platform.database.IDatabase;
import computer.lanoel.steam.SteamCache;

import java.io.File;

@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class Application {

public static void main(String[] args) {
		
		try
		{
			DatabaseUpgrader dbu = new DatabaseUpgrader(ServiceUtils.getDBConnection());
			File resFile = new File(Resources.getResource("database/mysql").getFile());
			if(dbu.upgradeDatabase(resFile.getAbsolutePath()))
			{
				throw new Exception("Could not upgrade database");
			}

			InitialPersonInfo.initializePlayerDb();
			SteamCache.instance().refreshFullSteamGameCache();
			SteamCache.instance().refresh();
			
		} catch (Exception ex){
			System.out.println("Error creating Database");
			ex.printStackTrace();
			System.out.println("Bye bye");
			return;
		}
		System.out.println(ServiceConstants.getServiceNameAsciiImage());
		
		SpringApplication.run(Application.class, args);

	}

}
