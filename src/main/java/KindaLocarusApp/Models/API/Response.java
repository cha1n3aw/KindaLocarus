package KindaLocarusApp.Models.API;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Response<T>
{
    private Integer responseStatus;
    private T responseData;
}
