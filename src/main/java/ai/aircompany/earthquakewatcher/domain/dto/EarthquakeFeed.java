package ai.aircompany.earthquakewatcher.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EarthquakeFeed(
        String type,
        Metadata metadata,
        List<Feature> features,
        List<Double> bbox
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(
            long generated,
            String url,
            String title,
            int status,
            String api,
            int count
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Feature(
            String type,
            Properties properties,
            Geometry geometry,
            String id
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(
            Double mag,
            String place,
            long time,
            long updated,
            Integer tz,
            String url,
            String detail,
            Integer felt,
            Double cdi,
            Double mmi,
            String alert,
            String status,
            int tsunami,
            int sig,
            String net,
            String code,
            String ids,
            String sources,
            String types,
            Integer nst,
            Double dmin,
            Double rms,
            Double gap,
            String magType,
            String type,
            String title
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(
            String type,
            List<Double> coordinates
    ) {
    }
}
