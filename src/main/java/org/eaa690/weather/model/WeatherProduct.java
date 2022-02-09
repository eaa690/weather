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

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * WeatherProduct.
 */
@Entity
@Table(name = "WEATHER_PRODUCT")
@Getter
@Setter
public class WeatherProduct extends BaseEntity {

    /**
     * One Hundred.
     */
    private static final int ONE_HUNDRED = 100;

    /**
     * Four Thousand.
     */
    private static final int FOUR_THOUSAND = 4000;

    /**
     * Default SerialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Key.
     */
    @Column(name = "k", length = ONE_HUNDRED)
    private String key;

    /**
     * Value.
     */
    @Column(name = "v", length = FOUR_THOUSAND)
    private String value;

    /**
     * Initializes an instance of <code>WeatherProduct</code> with the default data.
     */
    public WeatherProduct() {
        super.setCreatedAt(new Date());
        super.setUpdatedAt(new Date());
    }

}
