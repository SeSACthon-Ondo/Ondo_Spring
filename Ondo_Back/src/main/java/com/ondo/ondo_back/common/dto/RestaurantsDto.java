package com.ondo.ondo_back.common.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class RestaurantsDto {

    @CsvBindByName(column = "가맹점명")
    private String name;

    @CsvBindByName(column = "소재지도로명주소")
    private String address;

    @CsvBindByName(column = "위도")
    private double latitude;

    @CsvBindByName(column = "경도")
    private double longitude;

    @CsvBindByName(column = "카테고리")
    private String category;

    @CsvBindByName(column = "메뉴")
    private String menu;
}
