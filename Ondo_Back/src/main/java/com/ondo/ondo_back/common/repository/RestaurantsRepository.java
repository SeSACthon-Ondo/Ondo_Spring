package com.ondo.ondo_back.common.repository;

import com.ondo.ondo_back.common.entity.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantsRepository extends JpaRepository<Restaurants, Integer> {

    // 반경 500m 내 음식점 조회
    @Query("SELECT r FROM Restaurants r WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:longitude)) + sin(radians(:latitude)) * sin(radians(r.latitude)))) < 0.5")
    List<Restaurants> findbyNearbyRestaurants(Double latitude, Double longitude);
}
