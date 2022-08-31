package com.dst.smd207api.Controllers;

import com.dst.smd207api.Interfaces.Services.DeviceService;
import com.dst.smd207api.Models.CustomUser;
import com.dst.smd207api.Models.Response;
import com.dst.smd207api.Interfaces.Services.CustomUserService;
import com.dst.smd207api.Interfaces.Services.DeviceRawDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dst.smd207api.Constants.Constants.USERNAME_FIELD;
import static com.dst.smd207api.Constants.Constants.USERS_COLLECTION_NAME;

@RestController
@RequestMapping("/api")
public class DeviceRawDataController
{
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DeviceService deviceService;
    private final CustomUserService userService;
    private final DeviceRawDataService deviceRawDataService;

    @Autowired
    public DeviceRawDataController(DeviceService deviceService,
                                   CustomUserService userService,
                                   BCryptPasswordEncoder bCryptPasswordEncoder,
                                   MongoTemplate mongoTemplate,
                                   DeviceRawDataService deviceRawDataService)
    {
        this.deviceService = deviceService;
        this.deviceRawDataService = deviceRawDataService;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/devices.getPos")
    @ResponseBody
    public ResponseEntity<Response<?>> devicesGetPos(
            @RequestParam(required = true, name="imeis", defaultValue = "") List<String> imeis,
            @RequestParam(required = false, name="from") Instant fromTime,
            @RequestParam(required = false, name="to") Instant toTime)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
        {
            if (imeis == null || imeis.stream().count() == 0) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Correct IMEIs are required");}}, HttpStatus.OK);
            if (fromTime != null && toTime != null && fromTime.isAfter(toTime)) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect timestamps: 'From' should precede 'to'");}}, HttpStatus.OK);
            List<String> devices;
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
            {
                devices = new ArrayList<>(mongoTemplate.getCollectionNames().stream().filter(name -> name.matches("[0-9]{9}")).collect(Collectors.toList()));
                if (!imeis.contains("all")) devices.retainAll(imeis);
            }
            else
            {
                devices = new ArrayList<>(mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(authentication.getName())), CustomUser.class, USERS_COLLECTION_NAME).getDevices());
                if (!imeis.contains("all")) devices.retainAll(imeis);
            }
            if (devices == null || devices.stream().count() == 0 ) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Correct IMEIs are required");}}, HttpStatus.OK);
            else return new ResponseEntity<>(deviceRawDataService.devicesGetPos(devices, fromTime, toTime), HttpStatus.OK);
        }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @GetMapping("/devices.getTrack")
    @ResponseBody
    public ResponseEntity<Response<?>> devicesGetTrack(
            @RequestParam(required = true, name="imei", defaultValue = "") String imei,
            @RequestParam(required = false, name="from") Instant fromTime,
            @RequestParam(required = false, name="to") Instant toTime)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
        {
            if (imei == null || !imei.matches("[0-9]{9}")) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Correct IMEIs are required");}}, HttpStatus.OK);
            if (fromTime != null && toTime != null && toTime.isBefore(fromTime))
                return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect timestamps: 'From' should precede 'To'");}}, HttpStatus.OK);
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")) ||
                mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(authentication.getName())), CustomUser.class, USERS_COLLECTION_NAME).getDevices().contains(imei))
                    return new ResponseEntity<>(deviceRawDataService.devicesGetTrack(imei, fromTime, toTime), HttpStatus.OK);
                else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }
}
