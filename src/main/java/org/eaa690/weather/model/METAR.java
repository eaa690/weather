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

package org.eaa690.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * METAR observation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class METAR extends BaseEntity {

    /**
     * ICAO.
     */
    public static final String ICAO = "icao";

    /**
     * NAME.
     */
    public static final String NAME = "name";

    /**
     * OBSERVED.
     */
    public static final String OBSERVED = "observed";

    /**
     * RAW_TEXT.
     */
    public static final String RAW_TEXT = "raw_text";

    /**
     * BAROMETER.
     */
    public static final String BAROMETER = "barometer";

    /**
     * CEILING.
     */
    public static final String CEILING = "ceiling";

    /**
     * CLOUDS.
     */
    public static final String CLOUDS = "clouds";

    /**
     * DEWPOINT.
     */
    public static final String DEWPOINT = "dewpoint";

    /**
     * ELEVATION.
     */
    public static final String ELEVATION = "elevation";

    /**
     * FLIGHT_CATEGORY.
     */
    public static final String FLIGHT_CATEGORY = "flight_category";

    /**
     * HUMIDITY_PERCENT.
     */
    public static final String HUMIDITY_PERCENT = "humidity_percent";

    /**
     * TEMPERATURE.
     */
    public static final String TEMPERATURE = "temperature";

    /**
     * VISIBILITY.
     */
    public static final String VISIBILITY = "visibility";

    /**
     * WIND.
     */
    public static final String WIND = "wind";

    /**
     * Default SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ICAO Code.
     */
    private String icao;

    /**
     * Name.
     */
    private String name;

    /**
     * Observed.
     */
    private String observed;

    /**
     * Raw Text.
     */
    @JsonProperty("raw_text")
    private String rawText;

    /**
     * Barometer.
     */
    private Barometer barometer;

    /**
     * Ceiling.
     */
    private Ceiling ceiling;

    /**
     * list of Clouds.
     */
    private List<Cloud> clouds;

    /**
     * Dewpoint.
     */
    private Dewpoint dewpoint;

    /**
     * Elevation.
     */
    private Elevation elevation;

    /**
     * Flight Category.
     */
    @JsonProperty("flight_category")
    private String flightCategory;

    /**
     * Humidity Percent.
     */
    @JsonProperty("humidity_percent")
    private String humidityPercent;

    /**
     * Temperature.
     */
    private Temperature temperature;

    /**
     * Visibility.
     */
    private Visibility visibility;

    /**
     * Wind.
     */
    private Wind wind;

}
