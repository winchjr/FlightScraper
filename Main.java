package flightScraper;

import java.io.IOException;
import java.sql.SQLException;


//when called, scrapes mccarran airport arrivals and booking website for total amount of flights coming in, then sends to db

class Main {	
	
	public static void main (String[] args) throws InterruptedException, IOException, SQLException, ClassNotFoundException {

		scrapeFlights();
		scrapeHotels();
		callPython();

	}
	
	private static void scrapeFlights() throws IOException, InterruptedException {

		//set the total amount of scrapes
		final int totalScrapes = 3;
		
		//make a new flightscraper
		FlightScraper flightScraper = new FlightScraper();
				
		//scrape totalScrapes times
		for (int i = 0; i<totalScrapes; i++) {
			flightScraper.scrape();
		}
		//publish the flight totals, date to db
		flightScraper.publish();

	}
	//make new scraper, scrape, then publish
	private static void scrapeHotels() throws InterruptedException, SQLException, ClassNotFoundException {

		HotelScraper hotelScraper = new HotelScraper();
		hotelScraper.scrape();
		hotelScraper.publish();
	}
	private static void callPython() {
		
	}

}