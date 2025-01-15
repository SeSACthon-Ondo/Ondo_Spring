package com.ondo.ondo_back.common.service;

import com.ondo.ondo_back.common.dto.OnlineCulturalDto;
import com.ondo.ondo_back.common.entity.OnlineCultural;
import com.ondo.ondo_back.common.repository.OnlineCulturalRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OnlineCSVLoaderService {

    @Autowired
    private OnlineCulturalRepository onlineCulturalRepository;

    public void loadCSV(String filePath) throws IOException {

        try (FileReader fileReader = new FileReader(filePath)) {

            // CSV 파싱
            CsvToBean<OnlineCulturalDto> csvToBean = new CsvToBeanBuilder<OnlineCulturalDto>(fileReader)
                    .withType(OnlineCulturalDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<OnlineCulturalDto> onlineCulturalDtos = csvToBean.parse();

            // Dto -> Entity 변환 및 저장
            List<OnlineCultural> onlineCulturals = new ArrayList<>();

            for (OnlineCulturalDto OnlineDto : onlineCulturalDtos) {

                OnlineCultural onlineCultural = new OnlineCultural();
                onlineCultural.setArea(OnlineDto.getArea());
                onlineCultural.setName(OnlineDto.getName());
                onlineCultural.setCategory(OnlineDto.getCategory());
                onlineCultural.setAddress(OnlineDto.getAddress());
                onlineCultural.setPhone(OnlineDto.getPhone());

                onlineCulturals.add(onlineCultural);
            }

            onlineCulturalRepository.saveAll(onlineCulturals);
        }
    }
}
