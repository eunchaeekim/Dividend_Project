package com.example.dividend;

import com.example.dividend.model.Company;
import com.example.dividend.scraper.YahooFinanceScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DividendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DividendApplication.class, args);

        /*
		YahooFinanceScraper scraper = new YahooFinanceScraper();
		// var result = scraper.scrap(Company.builder().ticker("0").build());
		var result = scraper.scrapCompanyByTicker("MMM");
		System.out.println(result); // Company(ticker=MMM, name=3M Company)
		 */

		AutoComplete autoComplete = new AutoComplete();
		AutoComplete autoComplete1 = new AutoComplete();

		autoComplete.add("hello");
		System.out.println(autoComplete.get("hello"));
		System.out.println(autoComplete1.get("hello"));

	}
}
