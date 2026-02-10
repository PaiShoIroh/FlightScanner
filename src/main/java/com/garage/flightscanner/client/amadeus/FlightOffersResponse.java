package com.garage.flightscanner.client.amadeus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightOffersResponse {
    private List<FlightOffer> data;
    private Meta meta;
    private Dictionaries dictionaries;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlightOffer {
        private String id;
        private String source;
        private boolean instantTicketingRequired;
        private boolean nonHomogeneous;
        private boolean oneWay;
        private String lastTicketingDate;
        private int numberOfBookableSeats;
        private List<Itinerary> itineraries;
        private Price price;
        private List<TravelerPricing> travelerPricings;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Itinerary {
        private String duration;
        private List<Segment> segments;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Segment {
        private Location departure;
        private Location arrival;
        private String carrierCode;
        private String number;
        private Aircraft aircraft;
        private Operating operating;
        private String duration;
        private String id;
        private int numberOfStops;
        private boolean blacklistedInEU;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private String iataCode;
        private String terminal;
        private String at;  // ISO datetime
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Aircraft {
        private String code;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Operating {
        private String carrierCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Price {
        private String currency;
        private String total;
        private String base;
        private List<Fee> fees;
        private String grandTotal;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fee {
        private String amount;
        private String type;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TravelerPricing {
        private String travelerId;
        private String fareOption;
        private String travelerType;
        private Price price;
        private List<FareDetailsBySegment> fareDetailsBySegment;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FareDetailsBySegment {
        private String segmentId;
        private String cabin;
        private String fareBasis;
        private String brandedFare;
        private String classOfService;
        private IncludedCheckedBags includedCheckedBags;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IncludedCheckedBags {
        private Integer weight;
        private String weightUnit;
        private Integer quantity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meta {
        private int count;
        private Links links;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        private String self;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dictionaries {
        private java.util.Map<String, String> carriers;
        private java.util.Map<String, String> aircraft;
        private java.util.Map<String, String> currencies;
        private java.util.Map<String, LocationValue> locations;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationValue {
        private String cityCode;
        private String countryCode;
    }
}
