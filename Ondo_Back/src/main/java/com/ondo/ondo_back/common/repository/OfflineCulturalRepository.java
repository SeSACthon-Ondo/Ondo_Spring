package com.ondo.ondo_back.common.repository;

import com.ondo.ondo_back.common.entity.OfflineCultural;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfflineCulturalRepository extends JpaRepository<OfflineCultural, Integer> {

    List<OfflineCultural> findByAddressContaining(String district);
}
