package com.ondo.ondo_back.common.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class OfflineCulturalDto {

    @CsvBindByName(column = "지역")
    private String area;

    @CsvBindByName(column = "가맹점명")
    private String name;

    @CsvBindByName(column = "분류")
    private String category;

    @CsvBindByName(column = "주소")
    private String address;
}