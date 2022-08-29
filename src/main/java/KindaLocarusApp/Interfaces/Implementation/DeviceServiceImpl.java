package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Device;
import KindaLocarusApp.Models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.DefaultIndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import static KindaLocarusApp.Constants.Constants.*;

@Service
public class DeviceServiceImpl implements DeviceService
{
    private final MongoTemplate mongoTemplate;
    @Autowired
    public DeviceServiceImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

//    public Response<Device> getDevices(final List<String> imeies, final Boolean returnAll, final Boolean returnActive)
//    {
//
//        Response<Device> response = new Response<>();
//        try
//        {
//            /*
//            int errorsCount = 0;
//            String errorDesc = "";
//            for (String imei : imeies)
//            {
//                try
//                {
//                    Query query = new Query();
//                    query.addCriteria(Criteria.where("imei").is(imei));
//                    mongoTemplate.remove(query, imei);
//                }
//                catch (Exception e)
//                {
//                    errorsCount++;
//                    errorDesc += String.format("Failed to delete %s, reason: %s\n", username, e.getMessage());
//                }
//            }*/
//        }
//        catch (Exception e)
//        {
//            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s", e.getMessage(), e.getCause()));
//        }
//        return response;
//    }

    public Response<?> devicesGetInfo(final List<String> imeies, final List<String> fields)
    {
        Response<HashSet<Device>> response = new Response<>();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            HashSet<Device> devices = new HashSet<>();
            for (String imei : imeies)
            {
                try
                {
                    Device device = mongoTemplate.findOne(Query.query(Criteria.where("imei").is(imei)), Device.class);
                    if (device == null) throw new Exception("Unable to find specified device! ");
                    Device tempDevice = new Device();
                    if (fields != null)
                    {
                        int fieldErrorsCount = 0;
                        for (String field : fields)
                        {
                            try
                            {
                                Object fieldObject = device.getClass().getMethod("get" + StringUtils.capitalize(field), null).invoke(device);
                                Class[] methodArgs = new Class[1];
                                if (Objects.equals(fieldObject.getClass(), LinkedHashSet.class)) methodArgs[0] = Object.class;
                                else methodArgs[0] = fieldObject.getClass();
                                tempDevice.getClass().getMethod("set" + StringUtils.capitalize(field), methodArgs).invoke(tempDevice, fieldObject);
                            }
                            catch (Exception e)
                            {
                                fieldErrorsCount++;
                                errorDesc += String.format("Failed to fetch info on field %s for device %s, reason: %s : %s ", field, imei, e.getMessage(), e.getCause());
                            }
                        }
                        if (fieldErrorsCount > 0)
                        {
                            errorDesc += String.format("Overall failed to fetch info on %s fields for device %s ", fieldErrorsCount, imei);
                            response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                        }
                    }
                    else tempDevice = device;
                    devices.add(tempDevice);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to fetch info on device %s, reason: %s : %s", imei, e.getMessage(), e.getCause());
                }
            }
            if (errorsCount > 0)
            {
                errorDesc += String.format("Overall failed to fetch info on %s devices ", errorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(errorDesc += "OK");
            }
            response.setResponseData(devices);
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s ", e.getMessage(), e.getCause()));
        }
        return response;
    }
    public Response<?> devicesGetPos(final List<String> imeies, final Instant fromTime, final Instant toTime)
    {
        Response<Device> response = new Response<>();
        return response;
    }
    public Response<?> devicesGetTrack(final String imei, final Instant fromTime, final Instant toTime)
    {
        Response<Device> response = new Response<>();
        return response;
    }
    public Response<?> devicesAdd(final List<Device> newDevices)
    {
        Response<Device> response = new Response<>();
        return response;
    }
    public Response<?> devicesEdit(final List<Device> updatedDevices)
    {
        Response<Device> response = new Response<>();
        return response;
    }
    public Response<?> devicesDelete(final List<String> imeiesToDelete)
    {
        Response<Device> response = new Response<>();
        return response;
    }
}