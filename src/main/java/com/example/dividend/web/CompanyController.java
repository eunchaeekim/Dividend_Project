package com.example.dividend.web;

import com.example.dividend.model.Company;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<?> addCompany (@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName()); //회사를 저장할 때 마다 tire명이 저장됨

        return ResponseEntity.ok(company);
    }

    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        var result = this.companyService.getCompanyNamesByKeyword(keyword); //트라이에 따로 저장안해도됨 but db조회
//        var result = this.companyService.autocomplete(keyword);
        return ResponseEntity.ok(result);
    }


}
