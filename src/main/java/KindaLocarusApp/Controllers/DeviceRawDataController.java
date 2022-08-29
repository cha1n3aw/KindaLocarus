package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.Interfaces.Services.DeviceRawDataService;
import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Packet;
import KindaLocarusApp.Models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.USERNAME_FIELD;
import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

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
    public ResponseEntity<Response<?>> DevicesGetPos(
            @RequestParam(required = false, name="imeis") List<String> imeis,
            @RequestParam(required = false, name="from") Instant fromTime,
            @RequestParam(required = false, name="to") Instant toTime)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
        {
            List<String> devices;
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
            {
                if (imeis != null) devices = imeis;
                else
                {
                    Set<String> collectionNames = mongoTemplate.getCollectionNames();
                    for (String collectionName : collectionNames) if (!collectionName.matches("[0-9]{9}")) collectionNames.remove(collectionName);
                    devices = new ArrayList<>(collectionNames);
                }
            }
            else
            {
                devices = new ArrayList<>(mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(authentication.getName())), CustomUser.class, USERS_COLLECTION_NAME).getDevices());
                if (imeis != null) devices.retainAll(imeis);
            }
            return new ResponseEntity<>(deviceRawDataService.devicesGetPos(devices, fromTime, toTime), HttpStatus.OK);
        }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

//    DeviceGetTrack()
//        if (fromTime != null && toTime == null) toTime = fromTime.plus(30, ChronoUnit.DAYS);
//            else if (fromTime == null && toTime != null) fromTime = toTime.minus(30, ChronoUnit.DAYS);
//            else if (fromTime == null && toTime == null)
//    {
//        Query query = ;
//        mongoTemplate.findOne(new Query(){{
//            with(Sort.by(Sort.Direction.DESC, "idField"));
//            limit(1);
//        }}, Packet.class, );
//        toTime = fromTime.minus(30, ChronoUnit.DAYS);
//    }
}
