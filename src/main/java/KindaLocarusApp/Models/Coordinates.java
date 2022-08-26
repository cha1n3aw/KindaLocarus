package KindaLocarusApp.Models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
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

