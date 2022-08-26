package KindaLocarusApp.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "Devices")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device
{
    private String imei;
    private String type;
    private Boolean status;
    private Instant issueDate;
    private Instant expirationDate;
    private String deviceDescription;
    private List<String> log;   
}
