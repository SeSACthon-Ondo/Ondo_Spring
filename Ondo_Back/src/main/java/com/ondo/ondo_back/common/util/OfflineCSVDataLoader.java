package com.ondo.ondo_back.common.util;

import com.ondo.ondo_back.common.service.OfflineCSVLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OfflineCSVDataLoader implements CommandLineRunner {

    @Autowired
    private OfflineCSVLoaderService offlineCSVLoaderService;

    @Override
    public void run(String... args) throws Exception {

        String filePath = "src/main/resources/csvFiles/filtered_noori_offline_data_서울.csv";
        offlineCSVLoaderService.loadCSV(filePath);
    }
}
