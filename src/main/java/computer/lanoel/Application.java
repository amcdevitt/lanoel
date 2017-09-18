package computer.lanoel;


import com.google.common.io.Resources;
import com.omegasixcloud.database.DatabaseUpgrader;
import computer.lanoel.platform.ServiceUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import computer.lanoel.platform.InitialPersonInfo;
import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.steam.SteamCache;

import java.io.File;

@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class Application {

public static void main(String[] args) {
		
		try
		{
			DatabaseUpgrader.upgradeDatabase(ServiceConstants.sqlUpgradePaths, ServiceUtils.getDBConnection());

		} catch (Exception ex){
			System.out.println("Error creating Database");
			ex.printStackTrace();
			System.out.println("Bye bye");
			return;
		}

		try
		{
			InitialPersonInfo.initializePlayerDb();
			SteamCache.instance().refreshFullSteamGameCache();
			SteamCache.instance().refresh();
		} catch (Exception ex)
		{
			System.out.println("Could not initialize lanoel");
		}

		System.out.println(ServiceConstants.getServiceNameAsciiImage());
		
		SpringApplication.run(Application.class, args);

	}

}
