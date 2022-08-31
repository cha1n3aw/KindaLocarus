package com.dst.smd207api.Interfaces.Implementation;

import com.dst.smd207api.Interfaces.Services.DeviceService;
import com.dst.smd207api.Models.Device;
import com.dst.smd207api.Models.Response;
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

import static com.dst.smd207api.Constants.Constants.*;

@Service
public class DeviceServiceImpl implements DeviceService
{
    private final MongoTemplate mongoTemplate;
    @Autowired
    public DeviceServiceImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    public Boolean checkDeviceLicense(String imei) throws Exception
    {
        Device device = mongoTemplate.findOne(Query.query(Criteria.where(IMEI_FIELD).is(imei)), Device.class, DEVICES_COLLECTION_NAME);
        if (device == null) throw new Exception("Unable to find specified device! ", new Throwable("DEVICE_NOTFOUND"));
        if (device.getLicenseActive())
            if (device.getExpirationDate().isBefore(Instant.now()))
            {
                device.setLicenseActive(false);
                mongoTemplate.save(device);
                return false;
            }
            else return true;
        else
        {
            if (device.getExpirationDate().isAfter(Instant.now()))
            {
                device.setLicenseActive(true);
                mongoTemplate.save(device);
                return true;
            }
            else return false;
        }
    }

    public Response<?> devicesGet(final List<String> imeis, final List<String> fields)
    {
        Response<HashSet<Object>> response = new Response<>();
        try
        {
            int errorsCount = 0, totalFieldsErrorCount = 0;
            String errorDesc = "";
            HashSet<Object> devices = new HashSet<>();
            for (String imei : imeis)
            {
                try
                {
                    if (!checkDeviceLicense(imei)) devices.add(String.format("License for device with IMEI %s has expired, please, obtain a new one!", imei));
                    else
                    {
                        Device device = mongoTemplate.findOne(Query.query(Criteria.where(IMEI_FIELD).is(imei)), Device.class, DEVICES_COLLECTION_NAME);
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
            if (devices != null && devices.stream().count() > 0) response.setResponseData(devices);
            else response.setResponseData(null);
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s ", e.getMessage(), e.getCause()));
        }
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
                    if (device.getIssueDate().isAfter(device.getExpirationDate()))
                        return new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect dates: 'Issue date' should precede 'Expiration date'");}};
                    mongoTemplate.indexOps(DEVICES_COLLECTION_NAME).ensureIndex(new Index(IMEI_FIELD, Sort.Direction.DESC).unique());
                    mongoTemplate.insert(device);
                    checkDeviceLicense(device.getDeviceImei());
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
                    Device device = mongoTemplate.findOne(Query.query(Criteria.where(IMEI_FIELD).is(deviceUpdates.getDeviceImei())), Device.class, DEVICES_COLLECTION_NAME);
                    if (device == null) throw new Exception("Unable to find specified device! ", new Throwable("DEVICE_NOTFOUND"));
                    if (device.getIssueDate() != null && device.getExpirationDate() != null && device.getIssueDate().isAfter(device.getExpirationDate()))
                        return new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect dates: 'Issue date' should precede 'Expiration date'");}};

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

                                }
                            }
                        }
                    }
                    mongoTemplate.save(device);
                    checkDeviceLicense(device.getDeviceImei());
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

    public Response<?> devicesProlongLicense(final List<String> imeis, Instant issueDate, Instant expirationDate)
    {
        Response<String> response = new Response<>();
        String errorDesc = "";
        int modifiedCount = 0;
        try
        {
            if (imeis.contains("all"))
                for (Device device : mongoTemplate.findAll(Device.class, DEVICES_COLLECTION_NAME))
                {
                    device.setIssueDate(issueDate);
                    device.setExpirationDate(expirationDate);
                    if (issueDate.isBefore(expirationDate)) device.setLicenseActive(true);
                    else device.setLicenseActive(false);
                    mongoTemplate.save(device);
                }
            else
            {
                for (String imei : imeis)
                {
                    try
                    {
                        Device device = mongoTemplate.findOne(Query.query(Criteria.where(IMEI_FIELD).is(imei)), Device.class, DEVICES_COLLECTION_NAME);
                        if (device == null) throw new Exception("Unable to find specified device! ", new Throwable("DEVICE_NOTFOUND"));
                        else
                        {
                            device.setIssueDate(issueDate);
                            device.setExpirationDate(expirationDate);
                            if (issueDate.isBefore(expirationDate)) device.setLicenseActive(true);
                            else device.setLicenseActive(false);
                            mongoTemplate.save(device);
                            modifiedCount++;
                        }
                    }
                    catch (Exception e)
                    {
                        errorDesc += String.format("Failed to update license on imei %s, reason: %s ", imei, e.getMessage());
                    }
                }
            }
            if (!errorDesc.equals(""))
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
                response.setResponseData(String.format("Overall edited %s, failed %s ", modifiedCount, imeis.stream().count() - modifiedCount));
            }
            else
            {
                response.setResponseData("Updated successfully");
                response.setResponseErrorDesc(null);
                response.setResponseStatus(HttpStatus.OK.value());
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s ", e.getMessage(), e.getCause()));
        }
        return response;
    }
}