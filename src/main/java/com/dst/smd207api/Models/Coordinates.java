package com.dst.smd207api.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coordinates
{
    @Field("LON")
    private double lon;
    @Field("LAT")
    private double lat;
    @Field("ALT")
    private double alt;
    @Field("VEL")
    private double velocity;
}

