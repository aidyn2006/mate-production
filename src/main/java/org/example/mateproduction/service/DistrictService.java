package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.DistrictRequest;
import org.example.mateproduction.dto.response.DistrictResponse;
import org.example.mateproduction.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface DistrictService {
    DistrictResponse createDistrict(DistrictRequest request) throws NotFoundException;
    DistrictResponse getDistrictById(UUID id) throws NotFoundException;
    List<DistrictResponse> getAllDistricts();
    DistrictResponse updateDistrict(UUID id, DistrictRequest request) throws NotFoundException;
    void deleteDistrict(UUID id) throws NotFoundException;
}
