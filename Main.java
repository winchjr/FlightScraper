package flightScraper;

import java.io.IOException;


//when called, scrapes mccarran airport arrivals and exports data dump to postgresql for storage.

class Main {
		
	
	public static void main (String[] args) throws InterruptedException, IOException {
		
		final int totalScrapes = 5;
		//make a new scraper
		Scraper scraper = new Scraper();		
				
		//scrape five times
		for (int i = 0; i<totalScrapes; i++) {
		//tells the scraper to scrape
		scraper.scrape();				
	
		}
		//publish the flight totals, date, and ampm to the db
		scraper.publish();
		
	}
	
	

}
