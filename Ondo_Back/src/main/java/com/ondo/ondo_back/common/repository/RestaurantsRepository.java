package com.ondo.ondo_back.common.repository;

import com.ondo.ondo_back.common.entity.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantsRepository extends JpaRepository<Restaurants, Integer> {
}
