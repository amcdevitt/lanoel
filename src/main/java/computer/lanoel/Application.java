package computer.lanoel;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import computer.lanoel.platform.InitialPersonInfo;
import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.platform.database.DatabaseFactory;
import computer.lanoel.platform.database.IDatabase;
import computer.lanoel.steam.SteamCache;

@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class Application {

public static void main(String[] args) {
		
		try
		{
			IDatabase db = DatabaseFactory.getInstance().getDatabase("DEFAULT");
			db.UpgradeStorage();
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
