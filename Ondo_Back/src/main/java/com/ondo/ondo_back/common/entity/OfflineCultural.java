package com.ondo.ondo_back.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OfflineCultural {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "offline_id", nullable = false)
    private int id;

    @Column(name = "area")
    private String area;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "address")
    private String address;
}
