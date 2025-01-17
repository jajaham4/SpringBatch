package com.example.sample.repository;

import com.example.sample.entity.WinEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinRepository extends JpaRepository<WinEntity, Long> {
    // 페이징처리(pagealbe -> 스프링 프레임워크)
    Page<WinEntity> findByWinGreaterThanEqual(Long win, Pageable pageable);
}
