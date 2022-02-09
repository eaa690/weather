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

package org.eaa690.weather.steps;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.eaa690.weather.TestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

public class BaseSteps {

    /**
     * Server Base URI.
     */
    private final static String BASE_URI = "http://localhost";

    /**
     * Local Server Port.
     */
    @LocalServerPort
    private int port;

    /**
     * TestContext.
     */
    @Autowired
    protected final TestContext testContext;

    /**
     * Constructor.
     *
     * @param testContext TestContext
     */
    public BaseSteps(final TestContext testContext) {
        this.testContext = testContext;
    }

    /**
     * Configure RestAssured to use server base URI and local port.
     */
    private void configureRestAssured() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
    }

    protected RequestSpecification requestSpecification() {
        configureRestAssured();
        return given().log().all();
    }

}
