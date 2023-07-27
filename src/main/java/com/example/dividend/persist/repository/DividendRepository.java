package com.example.dividend.persist.repository;

import com.example.dividend.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    List<DividendEntity> findAllByCompanyId(Long companyId);
    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date);
    @Transactional
    void deleteAllByCompanyId(Long id);
}
