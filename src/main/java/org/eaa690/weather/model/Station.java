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
import lombok.Getter;
import lombok.Setter;

/**
 * Station object received from api.checkwx.com.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Station extends BaseEntity {

    /**
     * Default SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * ICAO.
     */
    private String icao;

    /**
     * Name.
     */
    private String name;

    /**
     * Activated.
     */
    private String activated;

    /**
     * City.
     */
    private String city;

    /**
     * Radius.
     */
    private Radius radius;

    /**
     * Elevation.
     */
    private Elevation elevation;

    /**
     * Country.
     */
    private Country country;

    /**
     * IATA.
     */
    private String iata;

    /**
     * Latitude.
     */
    private Coordinates latitude;

    /**
     * Longitude.
     */
    private Coordinates longitude;

    /**
     * Magnetic.
     */
    private String magnetic;

    /**
     * Sectional.
     */
    private String sectional;

    /**
     * State.
     */
    private State state;

    /**
     * Status.
     */
    private String status;

    /**
     * Timezone.
     */
    private Timezone timezone;

    /**
     * Type.
     */
    private String type;

    /**
     * Usage.
     */
    private String useage;

}
