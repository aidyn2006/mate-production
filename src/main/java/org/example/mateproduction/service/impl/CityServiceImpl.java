package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import org.example.mateproduction.dto.request.CityRequest;
import org.example.mateproduction.dto.response.CityResponse;
import org.example.mateproduction.entity.City;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.CityRepository;
import org.example.mateproduction.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;

    @Autowired
    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    @Override
    public CityResponse createCity(CityRequest request) {
        City city = City.builder()
                .name(request.getName())
                .build();

        city = cityRepository.save(city);

        return mapToResponse(city);
    }

    @Override
    public CityResponse getCityById(UUID id) throws NotFoundException {
        City city = (City) cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("City not found"));
        return mapToResponse(city);
    }

    @Override
    public List<CityResponse> getAllCities() {
        return cityRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CityResponse updateCity(UUID id, CityRequest request) throws NotFoundException {
        City city = (City) cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("City not found"));

        city.setName(request.getName());

        city = cityRepository.save(city);

        return mapToResponse(city);
    }

    @Transactional
    @Override
    public void deleteCity(UUID id) throws NotFoundException {
        City city = (City) cityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("City not found"));
        cityRepository.delete(city);
    }

    private CityResponse mapToResponse(City city) {
        return CityResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .build();
    }
}
