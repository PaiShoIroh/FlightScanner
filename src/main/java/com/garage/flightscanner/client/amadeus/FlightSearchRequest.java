package com.garage.flightscanner.client.amadeus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlightSearchRequest {
    private String origin;           // IATA code e.g., "JFK"
    private String destination;      // IATA code e.g., "LAX"
    private String departureDate;    // yyyy-MM-dd
    private String returnDate;       // yyyy-MM-dd (optional, for round trips)
    private int adults;
    private Integer maxResults;
    private boolean nonStop;
}
