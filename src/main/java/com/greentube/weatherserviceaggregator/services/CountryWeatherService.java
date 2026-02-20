package com.greentube.weatherserviceaggregator.services;

import com.greentube.weatherserviceaggregator.dtos.WeatherReport;
import com.greentube.weatherserviceaggregator.enums.Country;
import com.greentube.weatherserviceaggregator.infra.CountryWeatherServiceConnector;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CountryWeatherService {

    private static final Logger log =
            LoggerFactory.getLogger(CountryWeatherService.class);

    private final CountryWeatherServiceConnector maltaWeatherServiceConnector;
    private final CountryWeatherServiceConnector austriaWeatherServiceConnector;

    public CountryWeatherService(@Qualifier("maltaWeatherServiceConnector") CountryWeatherServiceConnector maltaWeatherServiceConnector,
                                 @Qualifier("austriaWeatherServiceConnector") CountryWeatherServiceConnector austriaWeatherServiceConnector) {
        this.maltaWeatherServiceConnector = maltaWeatherServiceConnector;
        this.austriaWeatherServiceConnector = austriaWeatherServiceConnector;
    }

//    @CircuitBreaker(name = "countryWeatherService", fallbackMethod = "fallback")
    public WeatherReport    getWeatherReportByCountry(Country country) {
        log.info("getWeatherReportByCountry request received for [country: {}]. Selecting Connector ...", country);
        WeatherReport weatherReport;
        // Select Connector
        switch (country) {
            case MT -> weatherReport = maltaWeatherServiceConnector.getWeatherReportForCountry();
            case AT -> weatherReport = austriaWeatherServiceConnector.getWeatherReportForCountry();
            default -> throw new UnsupportedOperationException("Connector Not Implemented");
        }
        return weatherReport;
    }

    public WeatherReport fallback(Country country, Throwable ex) {
        log.info("Fallback method");
        return WeatherReport.failed();
    }
}
