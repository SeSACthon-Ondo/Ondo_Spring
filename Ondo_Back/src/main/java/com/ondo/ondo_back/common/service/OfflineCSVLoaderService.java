package com.ondo.ondo_back.common.service;

import com.ondo.ondo_back.common.dto.OfflineCulturalDto;
import com.ondo.ondo_back.common.entity.OfflineCultural;
import com.ondo.ondo_back.common.repository.OfflineCulturalRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OfflineCSVLoaderService {

    @Autowired
    private OfflineCulturalRepository offlineCulturalRepository;

    public void loadCSV(String filePath) throws IOException {

        try (FileReader fileReader = new FileReader(filePath)) {

            // CSV 파싱
            CsvToBean<OfflineCulturalDto> csvToBean = new CsvToBeanBuilder<OfflineCulturalDto>(fileReader)
                    .withType(OfflineCulturalDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<OfflineCulturalDto> offlineCulturalDtos = csvToBean.parse();

            // Dto -> Entity 변환 및 저장
            List<OfflineCultural> offlineCulturals = new ArrayList<>();

            for (OfflineCulturalDto offlineDto : offlineCulturalDtos) {

                OfflineCultural offlineCultural = new OfflineCultural();
                offlineCultural.setArea(offlineDto.getArea());
                offlineCultural.setName(offlineDto.getName());
                offlineCultural.setCategory(offlineDto.getCategory());
                offlineCultural.setAddress(offlineDto.getAddress());

                offlineCulturals.add(offlineCultural);
            }

            offlineCulturalRepository.saveAll(offlineCulturals);
        }
    }
}
