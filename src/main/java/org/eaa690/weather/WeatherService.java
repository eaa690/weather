/*
 *  Copyright (C) 2021 Gwinnett County Experimental Aircraft Association
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.eaa690.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eaa690.weather.exception.InvalidPayloadException;
import org.eaa690.weather.exception.ResourceNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.eaa690.weather.model.Barometer;
import org.eaa690.weather.model.Ceiling;
import org.eaa690.weather.model.Cloud;
import org.eaa690.weather.model.Dewpoint;
import org.eaa690.weather.model.METAR;
import org.eaa690.weather.model.Temperature;
import org.eaa690.weather.model.Visibility;
import org.eaa690.weather.model.WeatherProduct;
import org.eaa690.weather.model.WeatherProductRepository;
import org.eaa690.weather.model.Wind;
import org.eaa690.weather.utils.SSLUtilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * WeatherController.
 */
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping({
        "/weather"
})
public class WeatherService {

    /**
     * METAR Key.
     */
    public static final String METAR_KEY = "METAR_";

    /**
     * Ten.
     */
    private static final int TEN = 10;

    /**
     * One Hundred.
     */
    private static final int ONE_HUNDRED = 100;

    /**
     * INVALID_STATION_MSG.
     */
    public static final String INVALID_STATION_MSG =
            "Provided station [%s] is not on the Atlanta sectional chart.  "
                    + "Please provide an accepted station identifier";

    /**
     * ATLANTA.
     */
    public static final String ATLANTA = "atlanta";

    /**
     * Synchronous rest template.
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * WeatherProperties.
     */
    @Autowired
    private WeatherProperties weatherProperties;

    /**
     * SSLUtilities.
     */
    @Autowired
    private SSLUtilities sslUtilities;

    /**
     * JSON Object Serializer/Deserializer.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * WeatherProductRepository.
     */
    @Autowired
    private WeatherProductRepository weatherProductRepository;

    /**
     * Updates weather information from AviationWeather.gov.
     * Note: normally this is run automatically every 10 minutes
     */
    @PostMapping(path = {"/update"})
    public void updateWeather() {
        update();
    }

    /**
     * Get METAR.
     *
     * Note: The only accepted station codes are those found on the Atlanta Sectional Chart
     *
     * @param icao station code
     * @param dataList attributes to be returned in response
     * @return METAR
     * @throws ResourceNotFoundException when METAR is not found
     * @throws InvalidPayloadException when an invalid station code is provided
     */
    @GetMapping(path = {
            "/metars/{icao}"
    })
    public List<METAR> metar(
            @PathVariable("icao") final String icao,
            @RequestParam(required = false, value = "data") final List<String> dataList)
            throws ResourceNotFoundException, InvalidPayloadException {
        final List<METAR> metars = new ArrayList<>();
        if (ATLANTA.equalsIgnoreCase(icao)) {
            Arrays.asList(weatherProperties.getAtlantaIcaoCodes().split(",")).forEach(code -> {
                try {
                    metars.add(getMETAR(code));
                } catch (ResourceNotFoundException e) {
                    log.warn("No METAR information found for ICAO Code {}", code);
                }
            });
        } else if (isValidStation(icao.toUpperCase())) {
            metars.addAll(List.of(getMETAR(icao.toUpperCase())));
        }
        if (CollectionUtils.isNotEmpty(metars)) {
            return filterAttributes(metars, dataList);
        }
        throw new InvalidPayloadException(String.format(INVALID_STATION_MSG, icao));
    }

    /**
     * Filters METAR attributes to only those specified, if any are specified.
     *
     * @param metars List of METAR
     * @param dataList attributes to be returned
     * @return filtered List of METAR
     */
    private static List<METAR> filterAttributes(final List<METAR> metars, final List<String> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return metars;
        }
        final List<METAR> filteredMetars = new ArrayList<>();
        for (final METAR metar : metars) {
            final METAR filteredMetar = new METAR();
            for (final String data : dataList) {
                filteredMetar.setIcao(metar.getIcao());
                switch (data) {
                    case METAR.OBSERVED:
                        filteredMetar.setObserved(metar.getObserved());
                        break;
                    case METAR.RAW_TEXT:
                        filteredMetar.setRawText(metar.getRawText());
                        break;
                    case METAR.BAROMETER:
                        filteredMetar.setBarometer(metar.getBarometer());
                        break;
                    case METAR.CEILING:
                        filteredMetar.setCeiling(metar.getCeiling());
                        break;
                    case METAR.CLOUDS:
                        filteredMetar.setClouds(metar.getClouds());
                        break;
                    case METAR.DEWPOINT:
                        filteredMetar.setDewpoint(metar.getDewpoint());
                        break;
                    case METAR.ELEVATION:
                        filteredMetar.setElevation(metar.getElevation());
                        break;
                    case METAR.FLIGHT_CATEGORY:
                        filteredMetar.setFlightCategory(metar.getFlightCategory());
                        break;
                    case METAR.HUMIDITY_PERCENT:
                        filteredMetar.setHumidityPercent(metar.getHumidityPercent());
                        break;
                    case METAR.TEMPERATURE:
                        filteredMetar.setTemperature(metar.getTemperature());
                        break;
                    case METAR.VISIBILITY:
                        filteredMetar.setVisibility(metar.getVisibility());
                        break;
                    case METAR.WIND:
                        filteredMetar.setWind(metar.getWind());
                        break;
                    default:
                        filteredMetar.setName(metar.getName());
                }
            }
            filteredMetars.add(filteredMetar);
        }
        return filteredMetars;
    }

    /**
     * Updates weather from aviationweather.gov.
     */
    @PostConstruct
    public void update() {
        getMETARsFromAviationWeather();
        // https://www.aviationweather.gov/cgi-bin/json/TafJSON.php?density=all&bbox=-85.6898,30.1588,-80.8209,35.1475
    }

    /**
     * Retrieves the current METAR for a given airport.
     *
     * @param icaoCode for the METAR observation
     * @return {@link METAR}
     * @throws ResourceNotFoundException when no information is found for the given ID
     */
    public METAR getMETAR(final String icaoCode) throws ResourceNotFoundException {
        METAR cachedMetar = null;
        Optional<WeatherProduct> weatherProductOpt =
                weatherProductRepository.findByKey(METAR_KEY + icaoCode);
        if (weatherProductOpt.isPresent()) {
            WeatherProduct weatherProduct = weatherProductOpt.get();
            try {
                cachedMetar = objectMapper.readValue(weatherProduct.getValue(), METAR.class);
            } catch (IOException e) {
                log.warn(String.format("Unable to deserialize METAR from cache: %s", e.getMessage()));
            }
        }
        if (cachedMetar != null) {
            return cachedMetar;
        }
        throw new ResourceNotFoundException(String.format("METAR information not found for %s", icaoCode));
    }

    /**
     * Checks if provided station is valid.
     *
     * @param station to be validated
     * @return if station is valid
     */
    public boolean isValidStation(final String station) {
        boolean response = false;
        final List<String> validStationsList = Arrays.asList(weatherProperties.getAtlantaIcaoCodes().split(","));
        if (validStationsList.contains(station)) {
            response = true;
        }
        return response;
    }

    /**
     * Queries AviationWeather.gov for METAR information.
     */
    private void getMETARsFromAviationWeather() {
        log.info("Querying AviationWeather.gov for METAR information");
        final String url = "https://www.aviationweather.gov/cgi-bin/json/MetarJSON.php"
                + "?density=all&bbox=-85.6898,30.1588,-80.8209,35.1475";
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final HttpEntity<String> headersEntity =  new HttpEntity<>("parameters", headers);
        // Ignoring SSL certificate checking
        sslUtilities.trustAllHostnames();
        sslUtilities.trustAllHttpsCertificates();
        try {
            final ResponseEntity<String> data =
                    restTemplate.exchange(url, HttpMethod.GET, headersEntity, String.class);
            if (data.getBody() != null
                    && data.getStatusCodeValue() >= HttpStatus.OK.value()
                    && data.getStatusCodeValue() < HttpStatus.MULTIPLE_CHOICES.value()) {
                JSONObject root = new JSONObject(new JSONTokener(data.getBody()));
                JSONArray features = root.getJSONArray("features");
                for (int i = 0; i < features.length(); i++) {
                    JSONObject station = features.getJSONObject(i);
                    if (station.has("id")) {
                        JSONObject props = station.getJSONObject("properties");
                        final METAR metar = parseMetar(props);
                        cacheMetar(metar.getIcao(), metar);
                    }
                }
            }
        } catch (RestClientException | JSONException rce) {
            String msg = String.format("[RestClientException] Unable to retrieve METARs: %s", rce.getMessage());
            log.error(msg, rce);
        }
    }

    /**
     * Parses METAR information from AviationWeather.gov response.
     *
     * @param props JSONObject
     * @return METAR
     */
    private METAR parseMetar(final JSONObject props) throws JSONException {
        final METAR metar = new METAR();
        metar.setIcao(props.getString(WeatherConstants.ID));
        metar.setObserved(props.getString(WeatherConstants.OBSERVED_TIME));
        if (props.has(WeatherConstants.TEMPERATURE)) {
            final Temperature temperature = new Temperature();
            temperature.setCelsius(Math.round(props.getDouble(WeatherConstants.TEMPERATURE)));
            metar.setTemperature(temperature);
        }
        if (props.has(WeatherConstants.DEWPOINT)) {
            final Dewpoint dewpoint = new Dewpoint();
            dewpoint.setCelsius(Math.round(props.getDouble(WeatherConstants.DEWPOINT)));
            metar.setDewpoint(dewpoint);
        }
        if (props.has(WeatherConstants.WIND_SPEED)) {
            final Wind wind = new Wind();
            wind.setSpeedKt(props.getInt(WeatherConstants.WIND_SPEED));
            wind.setDegrees(props.getInt(WeatherConstants.WIND_DIRECTION));
            metar.setWind(wind);
        }
        if (props.has(WeatherConstants.CEILING)) {
            final Ceiling ceiling = new Ceiling();
            ceiling.setFeet(props.getDouble(WeatherConstants.CEILING));
            ceiling.setCode(props.getString(WeatherConstants.COVER));
            metar.setCeiling(ceiling);
        }
        if (props.has(WeatherConstants.CLOUD_COVER + "1")) {
            final List<Cloud> clouds = new ArrayList<>();
            for (int j = 1; j < TEN; j++) {
                if (props.has(WeatherConstants.CLOUD_COVER + j)) {
                    final Cloud cloud = new Cloud();
                    cloud.setCode(props.getString(WeatherConstants.CLOUD_COVER + j));
                    if (props.has(WeatherConstants.CLOUD_BASE + j)) {
                        cloud.setBaseFeetAgl(Double.parseDouble(props.getString(WeatherConstants.CLOUD_BASE + j))
                                * ONE_HUNDRED);
                    }
                    clouds.add(cloud);
                }
            }
            metar.setClouds(clouds);
        }
        if (props.has(WeatherConstants.VISIBILITY)) {
            final Visibility visibility = new Visibility();
            visibility.setMiles(props.get(WeatherConstants.VISIBILITY).toString());
            metar.setVisibility(visibility);
        }
        if (props.has(WeatherConstants.FLIGHT_CATEGORY)) {
            metar.setFlightCategory(props.getString(WeatherConstants.FLIGHT_CATEGORY));
        }
        if (props.has(WeatherConstants.ALTIMETER)) {
            final Barometer barometer = new Barometer();
            barometer.setMb(props.getDouble(WeatherConstants.ALTIMETER));
            metar.setBarometer(barometer);
        }
        metar.setRawText(props.getString(WeatherConstants.RAW_OBSERVATION));
        metar.setCreatedAt(new Date());
        metar.setUpdatedAt(new Date());
        return metar;
    }

    /**
     * Caches METAR.
     *
     * @param icaoCode ICAO Code key for cached value
     * @param metar METAR to be cached
     */
    private void cacheMetar(final String icaoCode, final METAR metar) {
        try {
            WeatherProduct weatherProduct = new WeatherProduct();
            weatherProduct.setKey(METAR_KEY + icaoCode);
            weatherProduct.setCreatedAt(new Date());
            final Optional<WeatherProduct> weatherProductOpt =
                    weatherProductRepository.findByKey(METAR_KEY + icaoCode);
            if (weatherProductOpt.isPresent()) {
                weatherProduct = weatherProductOpt.get();
            }
            weatherProduct.setValue(objectMapper.writeValueAsString(metar));
            weatherProduct.setUpdatedAt(new Date());
            weatherProductRepository.save(weatherProduct);
        } catch (JsonProcessingException jpe) {
            log.warn(String.format("Unable to serialize METAR [%s]: %s", metar, jpe.getMessage()));
        }
    }

}
