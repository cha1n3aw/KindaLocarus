package com.dst.smd207api.Interfaces.Implementation;

import com.dst.smd207api.Constants.Constants;
import com.dst.smd207api.Models.Coordinates;
import com.dst.smd207api.Models.Device;
import com.dst.smd207api.Models.Packet;
import com.dst.smd207api.Models.Response;
import com.dst.smd207api.Interfaces.Services.DeviceRawDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.List;

import static com.dst.smd207api.Constants.Constants.*;

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
        Response<Map<String, HashSet<Packet>>> response = new Response<>();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            Map<String, HashSet<Packet>> imeiResponse = new HashMap<>();
            for (String imei : imeis)
            {
                HashSet<Packet> coordinates = new HashSet<>();
                try
                {
                    if (fromTime != null) coordinates.add(mongoTemplate.findOne(Query.query(Criteria.where("TIM").gte(fromTime)).limit(1).with(Sort.by(Sort.Direction.ASC, "TIM")), Packet.class, imei));
                    if (toTime != null) coordinates.add(mongoTemplate.findOne(Query.query(Criteria.where("TIM").lte(toTime)).limit(1).with(Sort.by(Sort.Direction.DESC, "TIM")), Packet.class, imei));
                    else if (fromTime == null) coordinates.add(mongoTemplate.findOne(Query.query(Criteria.where("TIM").ne(null)).limit(1).with(Sort.by(Sort.Direction.DESC, "TIM")), Packet.class, imei));
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to fetch position on device %s, reason: %s : %s ", imei, e.getMessage(), e.getCause());
                }
                if (coordinates != null && coordinates.stream().count() > 0) imeiResponse.put(imei, coordinates);
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
        Response<Device> response = new Response<>();

        return response;
    }
}
