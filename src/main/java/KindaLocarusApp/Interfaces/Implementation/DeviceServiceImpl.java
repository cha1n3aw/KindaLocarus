package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Models.Device;
import KindaLocarusApp.Models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;

import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

@Service
public class DeviceServiceImpl implements DeviceService
{
    private final MongoTemplate mongoTemplate;
    @Autowired
    public DeviceServiceImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    public Response<List<Device>> getDevices(final List<String> imeies, final boolean returnAll, final boolean returnActive)
    {
        Response<List<Device>> response = new Response<>();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            for (String imei : imeies)
            {
                try
                {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("imei").is(imei));
                    mongoTemplate.remove(query, imei);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to delete %s, reason: %s\n", username, e.getMessage());
                }
            }
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s", e.getMessage(), e.getCause()));
        }
        return response;
    }
}