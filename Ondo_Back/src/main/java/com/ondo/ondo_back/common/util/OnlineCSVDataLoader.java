package com.ondo.ondo_back.common.util;

import com.ondo.ondo_back.common.service.OnlineCSVLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OnlineCSVDataLoader implements CommandLineRunner {

    @Autowired
    private OnlineCSVLoaderService onlineCSVLoaderService;

    @Override
    public void run(String... args) throws Exception {

        String filePath = "src/main/resources/csvFiles/filtered_noori_online_data_서울.csv";
        onlineCSVLoaderService.loadCSV(filePath);
    }
}
