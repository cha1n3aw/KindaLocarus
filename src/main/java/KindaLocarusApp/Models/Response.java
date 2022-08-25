package KindaLocarusApp.Models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Response<T>
{
    private Integer responseStatus;
    private String responseErrorDesc;
    private T responseData;
}
