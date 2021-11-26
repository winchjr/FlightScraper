package flightScraper;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;


//Creates Scraper object, and also declares scraper methods including fetch (the website), parse (the webpage)

class Scraper{
	
	protected WebDriver driver;
	//builds the scraper
	public Scraper() {
        
		//setting up geckodriver
        System.setProperty("webdriver.gecko.driver","/home/owner/.mozilla/geckodriver/geckodriver");
        File pathBinary = new File("/usr/bin/firefox");
        FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);
        DesiredCapabilities desired = DesiredCapabilities.firefox();
        FirefoxProfile profile = new FirefoxProfile(new File("/home/owner/.mozilla/firefox/d5kmaqtb.GeckoDriver"));
        FirefoxOptions options = new FirefoxOptions();

        //options for geckodriver, prevents anonymous profile from being used.
        desired.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options.setBinary(firefoxBinary)); 
        options.addArguments("--profile", "/home/owner/.mozilla/firefox/d5kmaqtb.GeckoDriver");     
        options.setProfile(profile);
               
        driver = new FirefoxDriver();
	}
		

	
	//connection to local database
    protected static Connection getConnection() throws ClassNotFoundException, SQLException {

        Class.forName("org.postgresql.Driver");
        String userName = "postgres"; 
        String password = ""; 
        String hostname = "localhost"; 
        String port = "5432";
        String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/vegasstat";


        Connection con = DriverManager.getConnection(jdbcUrl, userName, password);

        return con;
  }

}
