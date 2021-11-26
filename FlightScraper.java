package flightScraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class FlightScraper extends Scraper implements ScraperActions{

    private static int flightTotal;
    private static final String INSERT_FLIGHT_TOTALS = "INSERT INTO flightdatasimple" +
            "  (date, totalflights) VALUES " +
            " (?, ?);";
    private static int currentFlightTotal = 0;
    WebDriverWait wait = new WebDriverWait(driver, 30);

    //builds the flightscraper by calling the superconstructor Scraper()
    public FlightScraper() {
        super();
    }

    //set flightDriver by ref of Main.driver
    private void setDriver(WebDriver newDriver) {
        driver = newDriver;
    }

    //fetch the website we are targeting for the scraper
    private WebDriver fetch () throws InterruptedException {

        //setting private variables.
        //WebDriverWait wait = new WebDriverWait(driver, 30);

        //get the website. everything below here has to be custom designed for each website.
        driver.get("https://mccarran.com/Flights/Arrivals");

        //respect network resources
        Thread.sleep((long) Math.floor(Math.random()*(1881)+.102)); //wait, for the specified time (between .102 and 1.982 seconds)
        Thread.sleep(15000); //TODO: remove
        //wait until the xpath for dropdown for time selection to appear, then select option 3 (all flights for today)
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"selArrivalDate\"]")));
        driver.findElement(By.xpath("//*[@id=\"selArrivalDate\"]")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"selArrivalDate\"]/option[3]")));
        driver.findElement(By.xpath("//*[@id=\"selArrivalDate\"]/option[3]")).click();
        Thread.sleep(13000); //TODO: remove
        return driver;
    }
    //retrieves the total scheduled flights for the day
    private int parse () {

        Scanner scan = new Scanner(driver.getPageSource());
        int count = 0;
        String input;

        //while we have page source to scan through, if the next string contains article, add 1 to count
        while (scan.hasNext()) {

            input = scan.next();

            if (input.contains("article")){
                count++;
            }

        }

        //every two article html tags are associated with 1 flight, so divide count by 2 to get the total flights for the day
        count = count / 2;
        return count;
    }

    public void scrape () throws InterruptedException{


        //set the driver using fetch()
        setDriver(fetch());


        //parse the data we want
        flightTotal = parse();

        //if the scraped flightTotal is larger than the current static flight total (which persists through each for loop in main) then make the currentflighttotal = to this highest flight total. This is in case of a bad scrape.
        if (flightTotal > currentFlightTotal) {
            currentFlightTotal = flightTotal;
        }

    }
    public void publish() throws IOException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        try {
            Connection conn = getConnection();
            //create prep stmt
            PreparedStatement preparedStatement = conn.prepareStatement(INSERT_FLIGHT_TOTALS); {
                preparedStatement.setString(1, date);
                preparedStatement.setInt(2, currentFlightTotal);
            }

            //update
            preparedStatement.executeUpdate();
        }

        catch (Exception e) {
            System.out.println("!!!!!!!!!!!!!!!! Error !!!!!!!!!!!!!!!!!");
            e.printStackTrace();
        }
        //we are done scraping and publishing, close the driver
        driver.close();
    }
}
