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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Wind observation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Wind implements Serializable {

    /**
     * Default SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Degrees.
     */
    private Integer degrees;

    /**
     * Speed Kt.
     */
    @JsonProperty("speed_kt")
    private Integer speedKt;

    /**
     * Speed Kts.
     */
    @JsonProperty("speed_kts")
    private Integer speedKts;

    /**
     * Speed MPH.
     */
    @JsonProperty("speed_mph")
    private Integer speedMph;

    /**
     * Speed MPS.
     */
    @JsonProperty("speed_mps")
    private Integer speedMps;

    /**
     * Gust Kt.
     */
    @JsonProperty("gust_kt")
    private Integer gustKt;

    /**
     * Gust MPH.
     */
    @JsonProperty("gust_mph")
    private Integer gustMph;

    /**
     * Gust MPS.
     */
    @JsonProperty("gust_mps")
    private Integer gustMps;

}
