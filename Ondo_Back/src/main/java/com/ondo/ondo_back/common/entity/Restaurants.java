package com.ondo.ondo_back.common.entity;

import com.ondo.ondo_back.common.util.MenuConvertor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Table(name = "restaurants")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Restaurants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurantId", nullable = false)
    private int restaurantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "category", nullable = false)
    private String category;

    @Lob
    @Column(name = "menu", columnDefinition = "TEXT")
    @Convert(converter = MenuConvertor.class)
    private Map<String, String> menu;
}
