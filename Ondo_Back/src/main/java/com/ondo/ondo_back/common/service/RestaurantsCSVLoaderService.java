package com.ondo.ondo_back.common.service;

import com.ondo.ondo_back.common.dto.RestaurantsDto;
import com.ondo.ondo_back.common.entity.Restaurants;
import com.ondo.ondo_back.common.repository.RestaurantsRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestaurantsCSVLoaderService {

    @Autowired
    private RestaurantsRepository restaurantsRepository;

    public void loadCSV(String filePath) throws IOException {

        try (FileReader fileReader = new FileReader(filePath)) {

            // CSV 파싱
            CsvToBean<RestaurantsDto> csvToBean = new CsvToBeanBuilder<RestaurantsDto>(fileReader)
                    .withType(RestaurantsDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<RestaurantsDto> restaurantsDtos = csvToBean.parse();

            // Dto -> Entity 변환 및 저장
            List<Restaurants> restaurants = new ArrayList<>();

            for (RestaurantsDto restaurantsDto : restaurantsDtos) {

                Restaurants restaurant = new Restaurants();
                restaurant.setName(restaurantsDto.getName());
                restaurant.setAddress(restaurantsDto.getAddress());
                restaurant.setLatitude(restaurantsDto.getLatitude());
                restaurant.setLongitude(restaurantsDto.getLongitude());
                restaurant.setCategory(restaurantsDto.getCategory());
                restaurant.setMenu(processMenu(restaurantsDto.getMenu()));

                restaurants.add(restaurant);
            }

            restaurantsRepository.saveAll(restaurants);
        }
    }

    private Map<String, String> processMenu(String menu) {

        if (menu == null || menu.isEmpty()) {

            return null;
        }

        Map<String, String> menuMap = new HashMap<>();
        String[] items = menu.split(";");
        StringBuilder processedMenu = new StringBuilder();

        for (String item : items) {

            String[] dishAndPrice = item.split(":");
            if (dishAndPrice.length == 2) {

                menuMap.put(dishAndPrice[0].trim(), dishAndPrice[1].trim());
            }
        }

        return menuMap;
    }
}
