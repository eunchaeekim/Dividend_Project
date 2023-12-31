package com.example.dividend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class DividendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DividendApplication.class, args);

        /*
		YahooFinanceScraper scraper = new YahooFinanceScraper();
		// var result = scraper.scrap(Company.builder().ticker("0").build());
		var result = scraper.scrapCompanyByTicker("MMM");
		System.out.println(result); // Company(ticker=MMM, name=3M Company)
		 */
		/*
		AutoComplete autoComplete = new AutoComplete();
		AutoComplete autoComplete1 = new AutoComplete();

		autoComplete.add("hello");
		System.out.println(autoComplete.get("hello"));
		System.out.println(autoComplete1.get("hello"));
		 */
	}
}
