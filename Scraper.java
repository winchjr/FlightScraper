package flightScraper;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

//Creates Scraper object, and also declares scraper methods including fetch (the website), parse (the webpage)
//TODO: most of this page can be packaged and put into a scraper tools package. fetch and parser need to be separated and can be packaged to some degree as well.

class Scraper{
	
	private WebDriver driver;
	
	//builds the scraper
	public Scraper() {
        //TODO: could add boolean anon var to tell me whether we need an anonymous profile or not  

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
	
	
	//set driver
	private void setDriver(WebDriver newDriver) {
		driver = newDriver;
	}
	
	//fetch the website we are targeting for the scraper
	private WebDriver fetch () throws InterruptedException {
    	
		//setting private variables.
    	WebDriverWait wait = new WebDriverWait(driver, 30); 
  	
		
    	//get the website. everything below here has to be custom designed for each website.
        driver.get("https://mccarran.com/Flights/Arrivals");           
        
        //respect network resources       
        Thread.sleep((long) Math.floor(Math.random()*(1881)+.102)); //wait, for the specified time (between .102 and 1.982 seconds)
        Thread.sleep(5000);
        //wait until the xpath for time status is available (which it should be after the thread.sleep above) and click it when it is
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"hdr_A_time\"]")));
        driver.findElement(By.xpath("//*[@id=\"hdr_A_time\"]")).click();
        Thread.sleep(3000);
		return driver;
	}
	
	//retrieves the total scheduled flights for the day
	private int parse () {
		
		Scanner scan = new Scanner(driver.getPageSource());		
		int count = 0;
		String input;
		Boolean correctTimeWindow = false;
		String ampm = "fm";
		Boolean keepGoing = true;
		Boolean checkAmPm = false;
		
		//while we have page source to scan through, if the next string contains article, add 1 to count
		while (scan.hasNext()) {
			
			input = scan.next();
	
			//if we are in teh correct time window and we see article, add 1 to count
			if (correctTimeWindow && input.contains("article")) {
				count++;
			}
			//if we are in the correct timewindow and the next input starts with a 12, check for ampm next round thru
			else if (correctTimeWindow && input.startsWith("12")) {
				checkAmPm = true;
			}
			//if we need to check for ampm go ahead and do so. if its false, turn off checkampm and keep going. else, end the correct time window
			else if (correctTimeWindow && checkAmPm) {
				input = (String) input.subSequence(0, 2); 

				
				//if the current string which contains the am or pm does not match our original one, then we can stop counting flights
				if (!input.equals(ampm)) {
					correctTimeWindow = false;
					keepGoing = false;					
				}
				
				//dont check for ampm any more, we are done counting flights
				checkAmPm = false;
			}
			
			//check for the correct time window
			else if (!correctTimeWindow && input.contains("<br>Scheduled:") && keepGoing) {

				scan.next();
				input = scan.next();
				
				//if the scheduled time contains a 11 then we have gotten to the section in the page source where our flight data needs to be scraped
				//the format in the pagesource is 10:12 PM
				if (input.startsWith("12")) {
					//we are in the correct time window now and can begin counting the flights coming in

					correctTimeWindow = true;
					
					//set whether we are looking at am or pm data so we know when to stop counting flights
					ampm = (String) scan.next().subSequence(0, 2);
					
				}
				
			}
		
		}
		
		//every two article html tags are associated with 1 flight, so divide count by 2 to get the total flights for the day
		count = count / 2;		
		driver.close();
		return count;
	}
	public int scrape () throws InterruptedException{
		
		//TODO: add error print stack trace log
		
		//fetch the website we want to scrape
		setDriver(fetch());
		
		//parse the data we want
		
		return parse();
		
		
		
	}
}