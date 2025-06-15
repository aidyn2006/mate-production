package org.example.mateproduction.controller.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.mateproduction.dto.request.AdHouseRequest;
import org.example.mateproduction.dto.response.AdHouseResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.exception.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface AdHouseControllerApi {

    @Operation(summary = "Получить все объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получено"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервиса"),

    })
    ResponseEntity<List<AdHouseResponse>> getAllAds();

    @Operation(summary = "Получить обявление по айдишке")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получено"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервиса"),

    })
    ResponseEntity<AdHouseResponse> getAdById(@PathVariable UUID id)  throws NotFoundException;

    @Operation(summary = "Создать обявление")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно создано"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервиса"),

    })

    ResponseEntity<AdHouseResponse> createAd(@ModelAttribute AdHouseRequest request) throws ValidationException, NotFoundException;

    @Operation(summary = "Обновить обявление")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно обновлено"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервиса"),
    })
    ResponseEntity<AdHouseResponse> updateAd(@PathVariable UUID id, @ModelAttribute AdHouseRequest request) throws NotFoundException, AccessDeniedException, ValidationException;

    @Operation(summary = "Удалить обявление")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно удалено"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервиса"),
    })
    ResponseEntity<Void> deleteAd(@PathVariable UUID id) throws AccessDeniedException, NotFoundException;

}
