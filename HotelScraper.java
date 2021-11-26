package flightScraper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

//things in here to add to scraper tools: variable sleep timing, addoneday
class HotelScraper extends Scraper implements ScraperActions{

	//private var
	private ArrayList<String> hotelUrlList = new ArrayList<String>();
	private ArrayList<Double> hotelPrices = new ArrayList<Double>();
	private ArrayList<String> hotelNames = new ArrayList<String>();
	private WebDriverWait wait = new WebDriverWait(driver, 30);
	private static final String INSERT_PRICES = "INSERT INTO hoteldata" +
			" (date, hotelname, price) VALUES " +
			" (?, ?, ?);";

	//constructor calls super and auto populates the url list and hotelnames list dynamically
	public HotelScraper() {
		super();
		hotelUrlList = populateUrlList();
		hotelNames = populateNamesList();
	}
	
	//set web driver 
	private void setDriver(WebDriver newDriver) {
		driver = newDriver;
	}

	//get dynamic hotel url, sleep for a variable time
	private WebDriver fetch (String url) throws InterruptedException {
		driver.get(url);
		Thread.sleep((long) Math.floor(Math.random()*(1881)+.102));
		return driver;
	}

	//get the string at the xpath with the price in it, remove the dollar sign, and convert it to a double
	private Double parse(){

		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[2]/main/div[2]/div/div/section[2]/div[2]/div[1]/div/div/span")));

			String price = driver.findElement(By.xpath("/html/body/div[2]/main/div[2]/div/div/section[2]/div[2]/div[1]/div/div/span")).getText().substring(1);

			//sometimes the xpath to the price varies, based on promotional offers etc. find the right using if stmt
			if (price.contains("%")) {
				return Double.parseDouble(driver.findElement(By.xpath("/html/body/div[2]/main/div[2]/div/div/section[2]/div[2]/div[1]/div/div[2]/span[2]")).getText().substring(1));
			} else {
				return Double.parseDouble(price);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return 00.00;
		}
	}

	//for each hotelUrl, set the web driver to the target url page, then put the contents of the parsing into the hotelPrices list, finally sleep for a variable time
	public void scrape () throws InterruptedException {
		for (String url : hotelUrlList) {
			setDriver(fetch(url));
			hotelPrices.add(parse());
			Thread.sleep((long) Math.floor(Math.random()*(1881)+.102));
		}
	}

	//publish the contents of hotelPrices to the DB
	public void publish() throws SQLException, ClassNotFoundException {
		Connection conn = getConnection();

		//while int i is less than hotel size, make a prep stmt using the hotelnames and hotelprices lists and execute each one
		for (int i = 0; i < hotelPrices.size(); i++){
			try {

				PreparedStatement preparedStatement = conn.prepareStatement(INSERT_PRICES); {
					preparedStatement.setString(1, LocalDate.now().toString());
					preparedStatement.setString(2, hotelNames.get(i));
					preparedStatement.setDouble(3, hotelPrices.get(i));
				}

				preparedStatement.executeUpdate();
			}

			catch (Exception e) {
				System.out.println("!!!!!!!!!!!!!!!! Error !!!!!!!!!!!!!!!!!");
				e.printStackTrace();
			}
		}

		//we are done scraping and publishing, close the driver
		driver.close();
	}
	
	//create an alist of dynamically created urls (that have the correct check in and checkout date)
	public ArrayList<String> populateUrlList (){

		ArrayList<String> tempList = new ArrayList<>();

		//adding the property tags manually
		tempList.add("ho108540"); //MGM Grand
		tempList.add("ho147594"); //venetian
		tempList.add("ho228169"); //wynn
		tempList.add("ho122212"); //Luxor
		//tempList.add("ho311298"); //aria
		tempList.add("ho163446"); //mandalay
		tempList.add("ho135542"); //excalibur
		tempList.add("ho124363"); //caesars
		tempList.add("ho119566"); //bellagio
		tempList.add("ho118583"); //circus circus
		tempList.add("ho112914"); //flamingo
		tempList.add("ho149069"); //mirage
		tempList.add("ho364984"); //cosmo
		tempList.add("ho123792"); //park mgm

		//get current date and format properly
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");  
		LocalDateTime now = LocalDateTime.now();  		
		String checkInDate = dtf.format(now);
		checkInDate = checkInDate.replaceAll("/", "-");	

		//create the next days date using addOneDay
		String checkOutDate = addOneDay(checkInDate);

		String urlStart = "https://hotels.com/";
		String urlEnd = "/?q-check-in=" + checkInDate + "&q-check-out=" + checkOutDate + "&q-rooms=1&q-room-0-adults=2&q-room-0-children=0&sort-order=BEST_SELLER";
		int i = 0;



		//for each prop tag string in temp list,
		for (String prop : tempList){			
			//rewrite the indexed position using a glued together url
			tempList.set(i, urlStart + prop + urlEnd);
			i++;			
		}

		return tempList;
	}
	public ArrayList<String> populateNamesList(){
		ArrayList<String> tempList = new ArrayList<>();

		tempList.add("MGM Grand");
		tempList.add("Venetian");
		tempList.add("Wynn");
		tempList.add("Luxor");
		//tempList.add("Aria");
		tempList.add("Mandalay Bay");
		tempList.add("Excalibur");
		tempList.add("Caesars");
		tempList.add("Bellagio");
		tempList.add("Circus Circus");
		tempList.add("Flamingo");
		tempList.add("Mirage");
		tempList.add("Cosmopolitan");
		tempList.add("Park MGM");

		return tempList;
	}
	//add one calendar day to the given date
	  static public String addOneDay(String date) {
		    return LocalDate.parse(date).plusDays(1).toString();
		  }
		}
	
	
