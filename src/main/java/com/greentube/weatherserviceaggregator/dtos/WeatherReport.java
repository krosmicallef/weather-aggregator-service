package com.greentube.weatherserviceaggregator.dtos;

import com.greentube.weatherserviceaggregator.enums.WeatherCondition;

import java.util.Date;

public record WeatherReport(byte temperature, WeatherCondition weatherCondition, Date date, int responseTime) {
    public static WeatherReport failed() {
        return new WeatherReport((byte)0, WeatherCondition.UNAVAILABLE, new Date(), 0);
    }
}
