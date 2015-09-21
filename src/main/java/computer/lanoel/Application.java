package computer.lanoel;


import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import computer.lanoel.platform.DatabaseManager;
import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.platform.ServiceUtils;

@ComponentScan
@EnableAutoConfiguration
public class Application {

public static void main(String[] args) {
		
		try
		{
			DatabaseManager db = new DatabaseManager(ServiceConstants.testUrl, 
					ServiceConstants.testUsername, ServiceConstants.testPassword);
			ServiceUtils.setStorage(db);
			db.UpgradeStorage();
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
