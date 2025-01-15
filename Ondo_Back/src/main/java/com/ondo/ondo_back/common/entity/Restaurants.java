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
    @Column(name = "restaurant_id", nullable = false)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "category")
    private String category;

    @Lob
    @Column(name = "menu", columnDefinition = "TEXT")
    @Convert(converter = MenuConvertor.class)
    private Map<String, String> menu;
}
