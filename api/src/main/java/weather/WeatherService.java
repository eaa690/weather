package weather;

import org.eaa690.common.exception.InvalidPayloadException;
import org.eaa690.common.exception.ResourceNotFoundException;
import weather.model.METAR;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping({
        "/weather"
})
public interface WeatherService {

    /**
     * Updates weather information from AviationWeather.gov.
     * Note: normally this is run automatically every 10 minutes
     */
    @PostMapping(path = {"/update"})
    void updateWeather();

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
    List<METAR> metar(
            @PathVariable("icao") final String icao,
            @RequestParam(required = false, value = "data") final List<String> dataList)
            throws ResourceNotFoundException, InvalidPayloadException;

    }
