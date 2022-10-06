package com.dst.smd207api.Interfaces.Implementation;

import com.dst.smd207api.Models.Device;
import com.dst.smd207api.Models.Packet;
import com.dst.smd207api.Models.Response;
import com.dst.smd207api.Interfaces.Services.DeviceRawDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import static com.dst.smd207api.Constants.Constants.DEVICES_COLLECTION_NAME;
import static com.dst.smd207api.Constants.Constants.IMEI_FIELD;

@Service
public class DeviceRawDataServiceImpl implements DeviceRawDataService
{
    private final MongoTemplate mongoTemplate;
    @Autowired
    public DeviceRawDataServiceImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    public Boolean checkDeviceLicense(Long imei) throws Exception
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

    public Response<?> devicesGetPos(final List<Long> imeis, final String mode, final Instant fromTime, final Instant toTime)
    {
//        Response<Map<Long, Map<Instant, Object>>> response = new Response<>();
        Response<Map<Long, List<Object>>> response = new Response<>();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
//            Map<Long, Map<Instant, Object>> imeiResponse = new HashMap<>();
            Map<Long, List<Object>> imeiResponse = new HashMap<>();
            for (Long imei : imeis)
            {
//                SortedMap<Instant, Object> coordinates = new TreeMap<>();
                List<Object> coordinates = new ArrayList<>();
                try
                {
//                    if (!checkDeviceLicense(imei)) coordinates.put(Instant.now(), String.format("License for device with IMEI %s has expired, please, obtain a new one!", imei));
                    if (!checkDeviceLicense(imei)) coordinates.add(String.format("License for device with IMEI %s has expired, please, obtain a new one!", imei));
                    else
                    {
                        Packet packet;
                        if (fromTime != null)
                        {
                            packet = mongoTemplate.findOne(Query.query(Criteria.where("TIM").gte(fromTime)).limit(1).with(Sort.by(Sort.Direction.ASC, "TIM")), Packet.class, imei.toString());
//                            if (mode.equals("full")) coordinates.put(packet.getTimestamp(), packet);
//                            else coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                            if (mode.equals("full")) coordinates.add(packet);
                            else coordinates.add(packet.getCoordinates());
                        }
                        if (toTime != null)
                        {
                            packet = mongoTemplate.findOne(Query.query(Criteria.where("TIM").gte(fromTime)).limit(1).with(Sort.by(Sort.Direction.DESC, "TIM")), Packet.class, imei.toString());
//                            if (mode.equals("full")) coordinates.put(packet.getTimestamp(), packet);
//                            else coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                            if (mode.equals("full")) coordinates.add(packet);
                            else coordinates.add(packet.getCoordinates());
                        }
                        else if (fromTime == null)
                        {
                            packet = mongoTemplate.findOne(Query.query(Criteria.where("TIM").ne(null)).limit(1).with(Sort.by(Sort.Direction.DESC, "TIM")), Packet.class, imei.toString());
//                            if (mode.equals("full")) coordinates.put(packet.getTimestamp(), packet);
//                            else coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                            if (mode.equals("full")) coordinates.add(packet);
                            else coordinates.add(packet.getCoordinates());
                        }
                    }
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to fetch position on device %s, reason: %s : %s ", imei, e.getMessage(), e.getCause());
                }
                if (coordinates.size() > 0) imeiResponse.put(imei, coordinates);
            }
            if (!Objects.equals(errorDesc, ""))
            {
                errorDesc += String.format("Overall failed to fetch position on %s devices ", errorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
            if (imeiResponse != null && imeiResponse.size() > 0) response.setResponseData(imeiResponse);
            else response.setResponseData(null);
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s ", e.getMessage(), e.getCause()));
        }
        return response;
    }

    public Response<?> devicesGetTrack(final Long imei, final String mode, Instant fromTime, Instant toTime)
    {
//        Response<Map<Long, List<Object>>> response = new Response<>();
        Response<List<Object>> response = new Response<>();
        try
        {
//            Map<Long, List<Object>> imeiResponse = new HashMap<>();
//            SortedMap<Instant, Object> coordinates = new TreeMap<>();
            List<Object> coordinates = new ArrayList<>();
            List<Object> imeiResponse = new ArrayList<>();
            try
            {
//                if (!checkDeviceLicense(imei)) coordinates.put(Instant.now(), String.format("License for device with IMEI %s has expired, please, obtain a new one!", imei));
                if (!checkDeviceLicense(imei)) coordinates.add(String.format("License for device with IMEI %s has expired, please, obtain a new one!", imei));
                else
                {
                    if (fromTime != null)
                        if (toTime != null)
                            for (Packet packet : mongoTemplate.find(Query.query(Criteria.where("TIM").gte(fromTime).lte(toTime)), Packet.class, imei.toString()))
                            {
//                                if (mode.equals("full")) coordinates.put(packet.getTimestamp(), packet);
//                                else coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                                if (mode.equals("full")) imeiResponse.add(packet);
                                else imeiResponse.add(packet.getCoordinates());
                            }
                        else
                            for (Packet packet : mongoTemplate.find(Query.query(Criteria.where("TIM").gte(fromTime).lte(fromTime.plus(30, ChronoUnit.DAYS))), Packet.class, imei.toString()))
                            {
//                                if (mode.equals("full")) coordinates.put(packet.getTimestamp(), packet);
//                                else coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                                if (mode.equals("full")) coordinates.add(packet);
                                else coordinates.add(packet.getCoordinates());
                            }
                    else if (toTime != null)
                        for (Packet packet : mongoTemplate.find(Query.query(Criteria.where("TIM").gte(toTime.minus(30, ChronoUnit.DAYS)).lte(toTime)), Packet.class, imei.toString()))
                        {
//                            if (mode.equals("full")) coordinates.put(packet.getTimestamp(), packet);
//                            else coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                            if (mode.equals("full")) coordinates.add(packet);
                            else coordinates.add(packet.getCoordinates());
                        }
                    else
                    {
                        Packet lastPacket = mongoTemplate.findOne(Query.query(Criteria.where("TIM").ne(null)).limit(1).with(Sort.by(Sort.Direction.DESC, "TIM")), Packet.class, imei.toString());
                        if (lastPacket != null)
                            for (Packet packet : mongoTemplate.find(Query.query(Criteria.where("TIM").gte(lastPacket.getTimestamp().minus(30, ChronoUnit.DAYS))), Packet.class, imei.toString()))
                            {
//                                if (mode.equals("full")) coordinates.put(packet.getTimestamp(), packet);
//                                else coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                                if (mode.equals("full")) coordinates.add(packet);
                                else coordinates.add(packet.getCoordinates());
                            }
                        else throw new Exception("No packets were found");
                    }
                }
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
            catch (Exception e)
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(String.format("Failed to fetch track on device %s, reason: %s : %s ", imei, e.getMessage(), e.getCause()));
                response.setResponseData(null);
            }
            if (imeiResponse.size() > 0)
            {
//                imeiResponse.put(imei, coordinates);
//                imeiResponse.add(coordinates);
                response.setResponseData(imeiResponse);
            }
            else response.setResponseData(null);
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s ", e.getMessage(), e.getCause()));
        }
        return response;
    }
}
