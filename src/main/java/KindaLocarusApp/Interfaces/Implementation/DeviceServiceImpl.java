package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Models.Device;
import KindaLocarusApp.Models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

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

    public Response<?> devicesGet(final List<String> imeis, final List<String> fields)
    {
        Response<HashSet<Device>> response = new Response<>();
        try
        {
            int errorsCount = 0, totalFieldsErrorCount = 0;
            String errorDesc = "";
            HashSet<Device> devices = new HashSet<>();
            for (String imei : imeis)
            {
                try
                {
                    Device device = mongoTemplate.findOne(Query.query(Criteria.where(IMEI_FIELD).is(imei)), Device.class, DEVICES_COLLECTION_NAME);
                    if (device == null) throw new Exception("Unable to find specified device! ", new Throwable("DEVICE_NOTFOUND"));
                    if (fields.contains("all")) devices.add(device);
                    else
                    {
                        Device tempDevice = new Device();
                        for (Field availableField : device.getClass().getDeclaredFields())
                        {
                            if (!Objects.equals(availableField.getName(), "_id"))
                            {
                                Object fieldObject = device.getClass().getMethod("get" + StringUtils.capitalize(availableField.getName()), null).invoke(device);
                                if (fields.contains(availableField.getName())) tempDevice.getClass().getMethod("set" + StringUtils.capitalize(availableField.getName()), new Class[]{fieldObject.getClass()}).invoke(tempDevice, fieldObject);
                            }
                        }
                        devices.add(tempDevice);
                    }
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to fetch info on device %s, reason: %s : %s ", imei, e.getMessage(), e.getCause());
                }
            }
            if (!Objects.equals(errorDesc, ""))
            {
                errorDesc += String.format("Overall failed to fetch info on %s fields for %s devices ", totalFieldsErrorCount, errorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
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
    public Response<?> devicesGetPos(final List<String> imeis, final Instant fromTime, final Instant toTime)
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
        Response<?> response = new Response();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            for (Device device : newDevices)
            {
                try
                {
                    device.setId(null);
                    mongoTemplate.indexOps(DEVICES_COLLECTION_NAME).ensureIndex(new Index(IMEI_FIELD, Sort.Direction.DESC).unique());
                    mongoTemplate.insert(device);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to add device %s, reason: %s : %s ", device.getDeviceImei(), e.getMessage(), e.getCause());
                }
            }
            if (!Objects.equals(errorDesc, ""))
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                errorDesc += String.format("Overall failed to add %s device(-s) ", errorsCount);
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s", e.getMessage(), e.getCause()));
        }
        return response;
    }
    public Response<?> devicesEdit(final List<Device> partialUpdates)
    {
        Response<?> response = new Response();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            for (Device deviceUpdates : partialUpdates)
            {
                try
                {
                    Query query = Query.query(Criteria.where(IMEI_FIELD).is(deviceUpdates.getDeviceImei()));
                    Device device = mongoTemplate.findOne(query, Device.class, DEVICES_COLLECTION_NAME);
                    if (device == null) throw new Exception("Unable to find specified device! ", new Throwable("DEVICE_NOTFOUND"));
                    for (Field field : deviceUpdates.getClass().getDeclaredFields())
                    {
                        if(!(Objects.equals(field.getName(), "_id")))
                        {
                            Object fieldObject = deviceUpdates.getClass().getMethod("get" + StringUtils.capitalize(field.getName()), null).invoke(deviceUpdates);
                            if (fieldObject != null)
                            {
                                try
                                {
                                    device.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), new Class[]{fieldObject.getClass()}).invoke(device, fieldObject);
                                }
                                catch (Exception e)
                                {
                                    System.out.println("WRONG DATE");
                                }
                            }
                        }
                    }
                    mongoTemplate.save(device);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to edit %s, reason: %s : %s", deviceUpdates.getDeviceImei(), e.getMessage(), e.getCause());
                }
            }
            if (!Objects.equals(errorDesc, ""))
            {
                errorDesc += String.format("Overall edited %s, failed %s ", partialUpdates.stream().count() - errorsCount, errorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s ", e.getMessage(), e.getCause()));
        }
        return response;
    }
    public Response<?> devicesDelete(final List<String> imeisToDelete)
    {
        Response<Device> response = new Response<>();
        return response;
    }
}