package com.greentube.weatherserviceaggregator.infra.impl;

import com.greentube.weatherserviceaggregator.dtos.WeatherReport;
import com.greentube.weatherserviceaggregator.enums.WeatherCondition;
import com.greentube.weatherserviceaggregator.infra.CountryWeatherServiceConnector;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class AustriaWeatherServiceConnector implements CountryWeatherServiceConnector {

    private static final Logger log =
            LoggerFactory.getLogger(AustriaWeatherServiceConnector.class);
    public static final String AT = "/AT";
    private final RestClient restClient;

    private record AustriaCurrentWeatherReport(int temperature, WeatherCondition weatherCondition) {}

    public AustriaWeatherServiceConnector(@Qualifier("pooledAustriaRestClient") RestClient austriaRestClient) {
        this.restClient = austriaRestClient;
    }

    @Override
    @CircuitBreaker(name = "austriaWeatherService", fallbackMethod = "fallback")
    public WeatherReport getWeatherReportForCountry() {
        AustriaCurrentWeatherReport currentWeatherReport;
        long ms;
        long start = System.nanoTime();
        try {
            currentWeatherReport = restClient.get().uri(AT).retrieve().body(AustriaCurrentWeatherReport.class);
        } finally {
            ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        }
        log.info("Current Weather Report [report: {}]", currentWeatherReport);
        assert currentWeatherReport != null;
        return new WeatherReport((byte) currentWeatherReport.temperature(), currentWeatherReport.weatherCondition(), new Date(), (int) ms);
    }

    public WeatherReport fallback() {
        log.info("Austria Fallback method");
        return WeatherReport.failed();
    }
}

