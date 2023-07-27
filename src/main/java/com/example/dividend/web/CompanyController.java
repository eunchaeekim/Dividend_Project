package com.example.dividend.web;

import com.example.dividend.model.Company;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    private final CacheManager redisCacheManager;

    @PostMapping
    @PreAuthorize("hasRole('WRITE')") // 해당 메서드를 호출하기 전에 사용자가 'WRITE'라는 역할(role)을 가져야만 접근을 허용
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
    @PreAuthorize("hasRole('READ')")
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

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        String companyName = this.companyService.deleteCompany(ticker);
        //캐시에서도 지워져야함
        this.clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName){
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName); // evict 메소드는 특정 키 또는 캐시를 캐시에서 제거하는 역할
    }


}
