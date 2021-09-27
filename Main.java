package flightScraper;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//when called, scrapes mccarran airport arrivals and exports data dump to postgresql for storage.

class Main {
	public static void main (String[] args) throws InterruptedException, IOException {
		
		//make a new scraper
		Scraper scraper = new Scraper();		
				
		//gets total flights by telling the scraper to scrape
		int flightTotal = scraper.scrape();
		
		//get local datetime and format
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		String submissionDate = dtf.format(now);
		String output = submissionDate + ": " + flightTotal;
		System.out.println(output);
		
		//store datetime and flighttotals to a text file
		FileWriter fw = new FileWriter("/home/owner/programmingprojects/bendProjectNotes/Flightscraper/flightdata.txt", true);
		fw.write(output + "\n");
		
		fw.close();
		//send the data to the database
		
		//calls python code for data analysis when complete
	}
	
	

}