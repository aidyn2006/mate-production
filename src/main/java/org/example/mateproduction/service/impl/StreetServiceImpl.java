package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.StreetRequest;
import org.example.mateproduction.dto.response.CityResponse;
import org.example.mateproduction.dto.response.DistrictResponse;
import org.example.mateproduction.dto.response.StreetResponse;
import org.example.mateproduction.entity.City;
import org.example.mateproduction.entity.District;
import org.example.mateproduction.entity.Street;
import org.example.mateproduction.repository.DistrictRepository;
import org.example.mateproduction.repository.StreetRepository;
import org.example.mateproduction.service.StreetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StreetServiceImpl implements StreetService {

    private final StreetRepository streetRepository;
    private final DistrictRepository districtRepository;

    @Override
    public StreetResponse create(StreetRequest request) {
        District district = districtRepository.findById(request.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found"));

        Street street = new Street();
        street.setName(request.getName());
        street.setDistrict(district);

        Street saved = streetRepository.save(street);
        return mapToResponse(saved);
    }

    @Override
    public StreetResponse update(UUID id, StreetRequest request) {
        Street street = streetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Street not found"));

        District district = districtRepository.findById(request.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found"));

        street.setName(request.getName());
        street.setDistrict(district);

        Street updated = streetRepository.save(street);
        return mapToResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        Street street = streetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Street not found"));
        streetRepository.delete(street);
    }

    @Override
    public StreetResponse getById(UUID id) {
        Street street = streetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Street not found"));
        return mapToResponse(street);
    }

    @Override
    public List<StreetResponse> getAll() {
        return streetRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private StreetResponse mapToResponse(Street street) {
        District district = street.getDistrict();
        City city = district.getCity();

        CityResponse cityResponse = CityResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .build();

        DistrictResponse districtResponse = DistrictResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .city(cityResponse)
                .build();

        return StreetResponse.builder()
                .id(street.getId())
                .name(street.getName())
                .districtId(districtResponse)
                .build();
    }

}
