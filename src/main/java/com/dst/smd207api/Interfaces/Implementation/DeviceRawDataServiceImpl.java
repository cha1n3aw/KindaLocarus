package com.dst.smd207api.Interfaces.Implementation;

import com.dst.smd207api.Models.Coordinates;
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

@Service
public class DeviceRawDataServiceImpl implements DeviceRawDataService
{
    private final MongoTemplate mongoTemplate;
    @Autowired
    public DeviceRawDataServiceImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    public Response<?> devicesGetPos(final List<String> imeis, final Instant fromTime, final Instant toTime)
    {
        Response<Map<String, Map<Instant, Coordinates>>> response = new Response<>();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            Map<String, Map<Instant, Coordinates>> imeiResponse = new HashMap<>();
            for (String imei : imeis)
            {
                SortedMap<Instant, Coordinates> coordinates = new TreeMap<>();
                try
                {
                    Packet packet;
                    if (fromTime != null)
                    {
                        packet = mongoTemplate.findOne(Query.query(Criteria.where("TIM").gte(fromTime)).limit(1).with(Sort.by(Sort.Direction.ASC, "TIM")), Packet.class, imei);
                        coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                    }
                    if (toTime != null)
                    {
                        packet = mongoTemplate.findOne(Query.query(Criteria.where("TIM").gte(fromTime)).limit(1).with(Sort.by(Sort.Direction.DESC, "TIM")), Packet.class, imei);
                        coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                    }
                    else if (fromTime == null)
                    {
                        packet = mongoTemplate.findOne(Query.query(Criteria.where("TIM").ne(null)).limit(1).with(Sort.by(Sort.Direction.DESC, "TIM")), Packet.class, imei);
                        coordinates.put(packet.getTimestamp(), packet.getCoordinates());
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

    public Response<?> devicesGetTrack(final String imei, final Instant fromTime, final Instant toTime)
    {
        Response<Map<String, Map<Instant, Coordinates>>> response = new Response<>();
        try
        {
            Map<String, Map<Instant, Coordinates>> imeiResponse = new HashMap<>();
            SortedMap<Instant, Coordinates> coordinates = new TreeMap<>();
            try
            {
                if (fromTime != null)
                    if (toTime != null)
                        for (Packet packet : mongoTemplate.find(Query.query(Criteria.where("TIM").gte(fromTime).lte(toTime)), Packet.class, imei))
                            coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                    else
                        for (Packet packet : mongoTemplate.find(Query.query(Criteria.where("TIM").gte(fromTime).lte(fromTime.plus(1, ChronoUnit.MONTHS))), Packet.class, imei))
                            coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                else
                    if (toTime != null)
                        for (Packet packet : mongoTemplate.find(Query.query(Criteria.where("TIM").gte(toTime.minus(1, ChronoUnit.MONTHS)).lte(toTime)), Packet.class, imei))
                            coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                    else
                        for (Packet packet : mongoTemplate.find(Query.query(Criteria.where("TIM").gte(Instant.now().minus(1, ChronoUnit.MONTHS))), Packet.class, imei))
                            coordinates.put(packet.getTimestamp(), packet.getCoordinates());
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
            catch (Exception e)
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(String.format("Failed to fetch track on device %s, reason: %s : %s ", imei, e.getMessage(), e.getCause()));
                response.setResponseData(null);
            }
            if (coordinates.size() > 0)
            {
                imeiResponse.put(imei, coordinates);
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
