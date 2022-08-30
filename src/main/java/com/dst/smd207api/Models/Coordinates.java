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
    @Field("LN")
    private double lon;
    @Field("LT")
    private double lat;
    @Field("AL")
    private double alt;
    @Field("VL")
    private double velocity;
}

