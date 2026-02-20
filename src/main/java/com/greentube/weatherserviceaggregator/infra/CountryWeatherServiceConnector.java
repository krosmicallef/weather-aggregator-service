package com.greentube.weatherserviceaggregator.infra;

import com.greentube.weatherserviceaggregator.dtos.WeatherReport;

public interface CountryWeatherServiceConnector {

    WeatherReport getWeatherReportForCountry();
}
