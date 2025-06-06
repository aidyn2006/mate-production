package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import org.example.mateproduction.dto.request.DistrictRequest;
import org.example.mateproduction.dto.response.CityResponse;
import org.example.mateproduction.dto.response.DistrictResponse;
import org.example.mateproduction.entity.City;
import org.example.mateproduction.entity.District;
import org.example.mateproduction.exception.AlreadyExistException;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.CityRepository;
import org.example.mateproduction.repository.DistrictRepository;
import org.example.mateproduction.service.DistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;
    private final CityRepository cityRepository;

    @Autowired
    public DistrictServiceImpl(DistrictRepository districtRepository, CityRepository city) {
        this.districtRepository = districtRepository;
        this.cityRepository = city;
    }

    @Transactional
    @Override
    public DistrictResponse createDistrict(DistrictRequest request) throws NotFoundException {
        districtRepository.findByName(request.getName())
                .ifPresent(existingDistrict -> {
                    try {
                        throw new AlreadyExistException("District with name '" + request.getName() + "' already exists.");
                    } catch (AlreadyExistException e) {
                        throw new RuntimeException(e);
                    }
                });

        City cityFromRequest = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new NotFoundException("City not found"));

        District district = District.builder()
                .name(request.getName())
                .city(cityFromRequest)
                .build();

        districtRepository.save(district);

        return mapToResponse(district);
    }

    @Override
    public DistrictResponse getDistrictById(UUID id) throws NotFoundException {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("District not found with id: " + id));
        return mapToResponse(district);
    }

    @Override
    public List<DistrictResponse> getAllDistricts() {
        return districtRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @Override
    public DistrictResponse updateDistrict(UUID id, DistrictRequest request) throws NotFoundException {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("District not found with id: " + id));

        districtRepository.findByName(request.getName())
                .filter(d -> !d.getId().equals(id))
                .ifPresent(existingDistrict -> {
                    try {
                        throw new AlreadyExistException("District with name '" + request.getName() + "' already exists.");
                    } catch (AlreadyExistException e) {
                        throw new RuntimeException(e);
                    }
                });

        City cityFromRequest = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new NotFoundException("City not found"));

        district.setName(request.getName());
        district.setCity(cityFromRequest);

        districtRepository.save(district);

        return mapToResponse(district);
    }

    @Transactional
    @Override
    public void deleteDistrict(UUID id) throws NotFoundException {
        District district = districtRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("District not found with id: " + id));
        districtRepository.delete(district);
    }


    private DistrictResponse mapToResponse(District district) {
        City city=district.getCity();
        CityResponse cityResponse=CityResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .build();

        return DistrictResponse.builder()
                .id(district.getId())
                .name(district.getName())
                .city(cityResponse)
                .build();
    }
}
