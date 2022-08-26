package KindaLocarusApp.Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
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
    private HashSet<String> analogChannels = new HashSet<String>();
    @Field("CRD")
    private Coordinates coordinates = new Coordinates();
    @Field("CST")
    private int gpsSatCount;
    @Field("CGS")
    private int glonassSatCount;
}
