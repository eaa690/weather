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

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.eaa690.weather.TestContext;
import org.hamcrest.Matchers;

/**
 * Weather test steps.
 */
public class WeatherSteps extends BaseSteps {

    /**
     * Weather service.
     */
    private final String WEATHER = "weather/";

    /**
     * Constructor.
     *
     * @param testContext TestContext
     */
    public WeatherSteps(final TestContext testContext) {
        super(testContext);
    }

    @Given("^I am an unauthenticated user$")
    public void unauthenticatedUser() {
        requestSpecification();
    }

    @Given("^I request the (.*) METAR wanting only (.*) information$")
    public void iOnlyWantSpecificInformation(final String icao, final String field) {
        testContext.setValidatableResponse(requestSpecification()
                .contentType(ContentType.JSON)
                .param("data", field)
                .when()
                .get(WEATHER + "metars/" + icao)
                .then().log().all());
    }

    @When("^I request the weather data to be updated$")
    public void iRequestWeatherUpdated() {
        testContext.setValidatableResponse(requestSpecification()
                .contentType(ContentType.JSON)
                .when()
                .post(WEATHER + "update")
                .then().log().all());
    }

    @When("^I request the (.*) METAR$")
    public void iRequestTheMETAR(final String icao) {
        testContext.setValidatableResponse(requestSpecification()
                .contentType(ContentType.JSON)
                .when()
                .get(WEATHER + "metars/" + icao)
                .then());
    }

    @When("^I request a METAR for an unprovided station$")
    public void iRequestDataForAnInvalidStation() {
        final String product = "metars/";
        testContext.setValidatableResponse(requestSpecification()
                .contentType(ContentType.JSON)
                .when()
                .get(WEATHER + product)
                .then().log().all());
    }

    @Then("^I should receive the (.*) data$")
    public void iShouldReceiveSpecificData(String field) {
        testContext.getValidatableResponse()
                .assertThat()
                .body(field, Matchers.notNullValue());
    }

    @Then("^I should receive data for multiple stations$")
    public void iShouldReceiveDataForMultipleStations() {
        testContext.getValidatableResponse()
                .assertThat()
                .body("size()", Matchers.greaterThan(1));
    }

    @Then("^The request should be successful$")
    public void requestSuccessful() {
        testContext.getValidatableResponse()
                .assertThat()
                .statusCode(Matchers.equalTo(HttpStatus.SC_OK));
    }

    @Then("^A (.*) exception should be thrown$")
    public void aExceptionShouldBeThrown(String exception) {
        if ("not found".equalsIgnoreCase(exception)) {
            testContext.getValidatableResponse()
                    .assertThat()
                    .statusCode(Matchers.equalTo(HttpStatus.SC_NOT_FOUND));
        }
        if ("bad request".equalsIgnoreCase(exception)) {
            testContext.getValidatableResponse()
                    .assertThat()
                    .statusCode(Matchers.equalTo(HttpStatus.SC_BAD_REQUEST));
        }
    }
}
