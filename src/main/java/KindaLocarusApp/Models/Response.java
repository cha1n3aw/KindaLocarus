package KindaLocarusApp.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T>
{
    private Integer responseStatus;
    private String responseErrorDesc;
    private T responseData;

    public void setResponseData(T responseData)
    {
        this.responseData = responseData;
    }
}
