package com.example.dividend.service;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.persist.repository.CompanyRepository;
import com.example.dividend.persist.repository.DividendRepository;
import com.example.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) { //클래스 밖에서 호출 불가능 save통해 호출 가능
        // ticker 를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker ->" + ticker);
        }

        //해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        //스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream() //.stream(): List<Dividend> 객체를 스트림으로 변환. 스트림은 데이터를 순차적으로 처리하는 기능을 제공.
                .map(e -> new DividendEntity(companyEntity.getId(), e)) // 각 Dividend 객체를 DividendEntity 객체로 변환하는 데 사용
                .collect(Collectors.toList()); // 스트림에서 처리한 결과를 리스트로 수집

        /* Stream 사용하지 않음
        List<DividendEntity> dividendEntityList = new ArrayList<>();
        List<Dividend> dividends = scrapedResult.getDividends();

        for (Dividend dividend : dividends) {
            DividendEntity dividendEntity = new DividendEntity(companyEntity.getId(), dividend);
            dividendEntityList.add(dividendEntity);
        }
         */

        this.dividendRepository.saveAll(dividendEntityList);
        return company;
    }
}