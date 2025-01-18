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
    @Column(name = "offlineId", nullable = false)
    private int offlineId;

    @Column(name = "area", nullable = false)
    private String area;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "address", nullable = false)
    private String address;
}
