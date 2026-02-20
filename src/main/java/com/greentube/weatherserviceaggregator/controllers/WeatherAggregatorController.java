package com.greentube.weatherserviceaggregator.controllers;

import com.greentube.weatherserviceaggregator.dtos.WeatherReport;
import com.greentube.weatherserviceaggregator.enums.Country;
import com.greentube.weatherserviceaggregator.enums.WeatherCondition;
import com.greentube.weatherserviceaggregator.services.CountryWeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather-aggregator")
public class WeatherAggregatorController {

    private static final Logger log =
            LoggerFactory.getLogger(WeatherAggregatorController.class);

    private final CountryWeatherService countryWeatherService;

    public WeatherAggregatorController(CountryWeatherService countryWeatherService) {
        this.countryWeatherService = countryWeatherService;
    }

    @RequestMapping("/report/{country}")
    public ResponseEntity<WeatherReport> getWeatherReport(@PathVariable Country country) {
        log.info("getWeatherReport request received for [country: {}]", country);
        WeatherReport weatherReport = countryWeatherService.getWeatherReportByCountry(country);
        HttpStatus httpStatus = weatherReport.weatherCondition() == WeatherCondition.UNAVAILABLE ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK;
        return ResponseEntity.status(httpStatus)
                .body(weatherReport);
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
