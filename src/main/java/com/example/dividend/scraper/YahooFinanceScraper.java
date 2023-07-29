package com.example.dividend.scraper;

import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper {

    // Yahoo Finance에서 배당금 데이터를 스크랩할 URL과 시작 시간을 상수로 정의
    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/COKE/history?period1=1618185600&period2=1649721600&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s/history?p=%s";

    private static final long START_TIME = 86400; // 60 * 60 * 24

    @Override
    public ScrapedResult scrap(Company company) {
        var scarpResult = new ScrapedResult(); // ScrapedResult 객체를 생성합니다.
        scarpResult.setCompany(company); // 파라미터로 받은 Company 객체를 ScrapedResult 객체에 설정합니다.

        try {
            long now = System.currentTimeMillis() / 1000; // 현재 시간을 초로 바꾸어 저장합니다.

            // URL에 Company 객체의 티커와 시작 시간, 현재 시간을 적용하여 최종 URL을 생성합니다.
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);

            // Jsoup을 사용하여 Yahoo Finance 페이지에 접속합니다.
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            // HTML에서 "data-test" 속성이 "historical-prices"인 요소를 찾아서 가져옵니다.
            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableEle = parsingDivs.get(0); // 해당 요소는 배당금 데이터가 포함된 테이블을 감싸고 있습니다.

            Element tbody = tableEle.children().get(1); // 테이블 요소에서 바디 부분을 추출합니다.

            List<Dividend> dividends = new ArrayList<>(); // 배당금 데이터를 저장할 리스트를 생성합니다.
            for (Element e : tbody.children()) {
                String txt = e.text();
                if (!txt.endsWith("Dividend")) {
                    continue;
                }

                // 각 행에서 배당금 데이터를 추출하여 Dividend 객체를 생성하고 리스트에 추가합니다.
                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.valueOf(splits[1].replace(",", ""));
                int year = Integer.valueOf(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);
                }

                dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));

            }

            // ScrapedResult 객체에 배당금 데이터를 설정합니다.
            scarpResult.setDividends(dividends);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ScrapedResult 객체를 반환합니다.
        return scarpResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0); // <h1 class="D(ib) Fz(16px) Lh(18px)">MMM - 3M Company</h1>
            String title = titleEle.text().split("-")[1].trim(); // 3M Company

            return new Company(ticker, title);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
