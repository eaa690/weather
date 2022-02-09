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

import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * WeatherProductRepository.
 */
public interface WeatherProductRepository extends Repository<WeatherProduct, Long> {
    /**
     * Gets a weather product.
     *
     * @param key Key
     * @return WeatherProduct
     */
    Optional<WeatherProduct> findByKey(String key);

    /**
     * Saves a weather product.
     *
     * @param weatherProduct WeatherProduct
     * @return WeatherProduct
     */
    WeatherProduct save(WeatherProduct weatherProduct);

}
