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

package weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eaa690.common.SSLUtilities;
import org.eaa690.common.exception.InvalidPayloadException;
import org.eaa690.common.exception.ResourceNotFoundException;
import weather.model.METAR;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class WeatherServiceImplTests {

    /**
     * RestTemplate.
     */
    @Mock
    RestTemplate restTemplate;

    /**
     * WeatherProperties.
     */
    @Mock
    WeatherProperties weatherProperties;

    /**
     * SSLUtilities.
     */
    @Mock
    SSLUtilities sslUtilities;

    /**
     * ObjectMapper.
     */
    @Mock
    ObjectMapper objectMapper;

    /**
     * WeatherProductRepository.
     */
    @Mock
    WeatherProductRepository weatherProductRepository;

    /**
     * WeatherService.
     */
    private WeatherService weatherService;

    /**
     * Test setup.
     *
     * @throws IOException when things go wrong
     */
    @Before
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);

        final File aviationWeatherResponse = new File("./src/test/resources/aviationweather_response.json");
        final BufferedReader reader = new BufferedReader(new FileReader(aviationWeatherResponse));
        final StringBuilder sb = new StringBuilder();
        while (reader.ready()) {
            sb.append(reader.readLine());
        }
        final ResponseEntity<String> response = ResponseEntity.of(Optional.of(sb.toString()));
        Mockito
                .doReturn(response)
                .when(restTemplate)
                .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class));

        Mockito
                .doReturn("KCNI,KGVL,KVPC,KJCA,KRYY,KLZU,KWDR,KPUJ,KMGE,KPDK,KFTY,"
                        + "KCTJ,KCVC,KATL,KCCO,KFFC,KHMP,KLGC,KOPN")
                .when(weatherProperties).getAtlantaIcaoCodes();

        Mockito.doNothing().when(sslUtilities).trustAllHostnames();
        Mockito.doNothing().when(sslUtilities).trustAllHttpsCertificates();

        Mockito.doReturn(new METAR()).when(objectMapper).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.doReturn("").when(objectMapper).writeValueAsString(Mockito.any());

        final WeatherProduct weatherProduct = new WeatherProduct();
        weatherProduct.setValue("{\"type\": \"Feature\",\"id\": \"803757662\",\"properties\": {\"data\": \"METAR\",\"id\": \"KATL\",\"site\": \"Atlanta/Hartsfield I\",\"prior\": 0,\"obsTime\": \"2022-02-27T16:04:00Z\",\"temp\": 9.4,\"dewp\": 8.3,\"wspd\": 4,\"wdir\": 120,\"ceil\": 7,\"cover\": \"OVC\",\"cldCvg1\": \"BKN\",\"cldBas1\": \"7\",\"cldCvg2\": \"OVC\",\"cldBas2\": \"26\",\"visib\": 2.50,\"fltcat\": \"IFR\",\"altim\": 1022.4,\"wx\": \"RA BR\",\"rawOb\": \"KATL 271604Z 12004KT 2 1/2SM RA BR BKN007 OVC026 09/08 A3019 RMK AO2 P0002 T00940083 $\"},\"geometry\": {\"type\": \"Point\",\"coordinates\": [-84.442,33.630]}}");
        Mockito.doReturn(Optional.of(weatherProduct)).when(weatherProductRepository).findByKey(Mockito.anyString());

        Mockito.doReturn(weatherProduct).when(weatherProductRepository).save(Mockito.any());

        weatherService = new WeatherServiceImpl(restTemplate, weatherProperties, sslUtilities,
                objectMapper, weatherProductRepository);
    }

    /**
     * Test update.
     */
    @Test
    public void testUpdate() throws JsonProcessingException {
        weatherService.updateWeather();

        Mockito.verify(restTemplate, Mockito.times(1))
                .exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class));
        Mockito.verifyNoMoreInteractions(restTemplate);

        Mockito.verify(sslUtilities, Mockito.times(1)).trustAllHostnames();
        Mockito.verify(sslUtilities, Mockito.times(1)).trustAllHttpsCertificates();
        Mockito.verifyNoMoreInteractions(sslUtilities);

        Mockito.verify(objectMapper, Mockito.atLeast(1)).writeValueAsString(Mockito.any());
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.atLeast(1)).findByKey(Mockito.anyString());
        Mockito.verify(weatherProductRepository, Mockito.atLeast(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(weatherProperties);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", null);

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_Atlanta() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("ATLANTA", null);

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.atLeast(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.atLeast(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_ObservedFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("observed"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_RawTextFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("raw_text"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_BarometerFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("barometer"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_CeilingFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("ceiling"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_CloudsFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("clouds"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_DewpointFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("dewpoint"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_ElevationFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("elevation"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_FlightCategoryFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("flight_category"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_HumidityPercentFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("humidity_percent"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_TemperatureFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("temperature"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_VisibilityFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("visibility"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_WindFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("wind"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test
    public void testMetar_UnknownFilter() throws ResourceNotFoundException, InvalidPayloadException, JsonProcessingException {
        weatherService.metar("KATL", List.of("unknown"));

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verify(objectMapper, Mockito.times(1)).readValue(Mockito.anyString(), Mockito.eq(METAR.class));
        Mockito.verifyNoMoreInteractions(objectMapper);

        Mockito.verify(weatherProductRepository, Mockito.times(1)).findByKey(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(weatherProductRepository);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities);
    }

    /**
     * Test metar.
     *
     * @throws ResourceNotFoundException when things go wrong
     * @throws InvalidPayloadException when things go wrong
     */
    @Test(expected = InvalidPayloadException.class)
    public void testMetar_InvalidStation() throws ResourceNotFoundException, InvalidPayloadException {
        weatherService.metar("KDEN", null);

        Mockito.verify(weatherProperties, Mockito.times(1)).getAtlantaIcaoCodes();
        Mockito.verifyNoMoreInteractions(weatherProperties);

        Mockito.verifyNoInteractions(restTemplate, sslUtilities, objectMapper, weatherProductRepository);
    }
}
