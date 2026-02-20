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
public class MaltaWeatherServiceConnector implements CountryWeatherServiceConnector {

    private static final Logger log =
            LoggerFactory.getLogger(MaltaWeatherServiceConnector.class);
    public static final String MT = "/MT";
    private final RestClient restClient;

    private record MaltaCurrentWeatherReport(int temperature, WeatherCondition weatherCondition) {}

    public MaltaWeatherServiceConnector(@Qualifier("pooledMaltaRestClient") RestClient maltaRestClient) {
        this.restClient = maltaRestClient;
    }

    @Override
    @CircuitBreaker(name = "maltaWeatherService", fallbackMethod = "fallback")
    public WeatherReport getWeatherReportForCountry() {
        MaltaCurrentWeatherReport currentWeatherReport;
        long ms;
        long start = System.nanoTime();
        try {
            currentWeatherReport = restClient.get().uri(MT).retrieve().body(MaltaCurrentWeatherReport.class);
        } finally {
            ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        }
        log.info("Current Weather Report [report: {}]", currentWeatherReport);
        assert currentWeatherReport != null;
        return new WeatherReport((byte) currentWeatherReport.temperature(), currentWeatherReport.weatherCondition(), new Date(), (int) ms);
    }

    public WeatherReport fallback() {
        log.info("Malta Fallback method");
        return WeatherReport.failed();
    }
}


