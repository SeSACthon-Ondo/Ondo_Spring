package com.ondo.ondo_back.common.util;

import com.ondo.ondo_back.common.service.RestaurantsCSVLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RestaurantsCSVDataLoader implements CommandLineRunner {

    @Autowired
    private RestaurantsCSVLoaderService restaurantsCsvLoaderService;

    @Override
    public void run(String... args) throws Exception {

        String filePath = "src/main/resources/csvFiles/filtered_restaurant_data_광진구.csv";
        restaurantsCsvLoaderService.loadCSV(filePath);
    }
}
