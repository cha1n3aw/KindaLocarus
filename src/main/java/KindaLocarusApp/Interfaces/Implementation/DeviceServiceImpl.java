package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Device;
import KindaLocarusApp.Models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.DefaultIndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
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

    public Response<?> devicesGetInfo(final List<String> imeies, final List<String> fields)
    {
        Response<HashSet<Device>> response = new Response<>();
        try
        {
            int errorsCount = 0, totalFieldsErrorCount = 0;
            String errorDesc = "";
            HashSet<Device> devices = new HashSet<>();
            if (imeies == null) devices.addAll(mongoTemplate.findAll(Device.class, DEVICES_COLLECTION_NAME));
            else
            {
                for (String imei : imeies)
                {
                    try
                    {
                        Device device = mongoTemplate.findOne(Query.query(Criteria.where(IMEI_FIELD).is(imei)), Device.class, DEVICES_COLLECTION_NAME);
                        if (device == null) throw new Exception("Unable to find specified device! ", new Throwable("DEVICE_NOTFOUND"));
                        Device tempDevice = new Device();
                        if (fields != null)
                        {
                            int fieldErrorsCount = 0;
                            for (String field : fields)
                            {
                                try
                                {
                                    Object fieldObject = device.getClass().getMethod("get" + StringUtils.capitalize(field), null).invoke(device);
                                    tempDevice.getClass().getMethod("set" + StringUtils.capitalize(field), new Class[] {fieldObject.getClass()}).invoke(tempDevice, fieldObject);
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
                                totalFieldsErrorCount+=fieldErrorsCount;
                            }
                        }
                        else tempDevice = device;
                        devices.add(tempDevice);
                    }
                    catch (Exception e)
                    {
                        errorsCount++;
                        errorDesc += String.format("Failed to fetch info on device %s, reason: %s : %s ", imei, e.getMessage(), e.getCause());
                    }
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
            response.setResponseErrorDesc(String.format("Internal server error, reason: ", e.getMessage()));
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
                    Query query = Query.query(Criteria.where(ID_FIELD).is(deviceUpdates.getId()));
                    Device device = mongoTemplate.findOne(query, Device.class, DEVICES_COLLECTION_NAME);
                    if (device == null) throw new Exception("Unable to find specified device! ", new Throwable("DEVICE_NOTFOUND"));
                    for (Field field : deviceUpdates.getClass().getDeclaredFields())
                    {
                        if(!(Objects.equals(field.getName(), "_id")))
                        {
                            Object fieldObject = deviceUpdates.getClass().getMethod("get" + StringUtils.capitalize(field.getName()), null).invoke(deviceUpdates);
                            if (fieldObject != null) device.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), new Class[]{fieldObject.getClass()}).invoke(device, fieldObject);
                        }
                    }
                    mongoTemplate.save(device);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to edit %s, reason: %s ", deviceUpdates.getId(), e.getMessage());
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
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s ", e.getMessage()));
        }
        return response;
    }
    public Response<?> devicesDelete(final List<String> imeiesToDelete)
    {
        Response<Device> response = new Response<>();
        return response;
    }
}