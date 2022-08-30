package com.dst.smd207api.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Packet
{
    @Field("TIM")
    private Instant timestamp;
    @Field("VLT")
    private double measuredVoltage;
    @Field("FLG")
    private byte stateFlags;
    @Field("UCH")
    private int usedChannels;
    @Field("ACH")
    private Map<Integer, Integer> analogChannels;
    @Field("CRD")
    private Coordinates coordinates;
    @Field("GPS")
    private int gpsSatCount;
    @Field("GLS")
    private int glonassSatCount;
}
