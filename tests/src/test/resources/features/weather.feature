@weather
Feature: weather
  METAR information

  @update
  Scenario: Update weather data from AviationWeather.gov
    Given I am an unauthenticated user
    When I request the weather data to be updated
    Then The request should be successful

  @metar
  Scenario Outline: Retrieve METAR information for a single station
    Given I am an unauthenticated user
    When I request the <icao> METAR
    Then The request should be successful

    Examples:
      | icao  |
      | KATL  |
      | KLZU  |
      | KFFC  |

  @metar
  Scenario: Retrieve METAR information for the Atlanta TAC
    Given I am an unauthenticated user
    When I request the Atlanta METAR
    Then The request should be successful
    And I should receive data for multiple stations

  @metar
  Scenario: Retrieve METAR information for an unprovided station
    Given I am an unauthenticated user
    When I request a METAR for an unprovided station
    Then A not found exception should be thrown

  @metar
  Scenario Outline: Retrieve specific METAR information for a single station
    Given I am an unauthenticated user
    When I request the <icao> METAR wanting only <field> information
    Then The request should be successful
    And I should receive the <field> data

    Examples:
      | icao  | field            |
      | KATL  | flight_category  |
      | KFFC  | observed         |
      | KLZU  | raw_text         |
      | KLZU  | name             |
      | KLZU  | observed         |
      | KLZU  | barometer        |
      | KLZU  | ceiling          |
      | KLZU  | clouds           |
      | KLZU  | dewpoint         |
      | KLZU  | elevation        |
      | KLZU  | humidity_percent |
      | KLZU  | temperature      |
      | KLZU  | visibility       |
      | KLZU  | wind             |

  @metar
  Scenario Outline: Retrieve METAR information for an invalid station
    Given I am an unauthenticated user
    When I request the <icao> METAR
    Then A bad request exception should be thrown

    Examples:
      | icao  |
      | KCLT  |
      | KDEN  |
