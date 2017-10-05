package computer.lanoel;


import com.omegasixcloud.database.DatabaseUpgrader;
import computer.lanoel.platform.ServiceUtils;
import computer.lanoel.platform.database.DBConnection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import computer.lanoel.platform.InitialPersonInfo;
import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.steam.SteamCache;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class Application {

public static void main(String[] args) throws Exception {
		
		try
		{
			DatabaseUpgrader.upgradeDatabase(ServiceConstants.sqlUpgradePaths, ServiceUtils.getSingleDBConnection());

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
			ex.printStackTrace();
			throw ex;
		}

		System.out.println(ServiceConstants.getServiceNameAsciiImage());
		
		SpringApplication.run(Application.class, args);

	}

}
