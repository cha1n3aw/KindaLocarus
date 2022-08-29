package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.Interfaces.Services.DeviceRawDataService;
import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;
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
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities() != null && authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(deviceRawDataService.devicesGetPos(imeis, fromTime, toTime), HttpStatus.OK);
            else
            {
                HashSet<String> devices = mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(authentication.getName())), CustomUser.class, USERS_COLLECTION_NAME).getDevices();
                if (imeis != null) devices.retainAll(imeis);
                return new ResponseEntity<>(deviceRawDataService.devicesGetPos(new ArrayList<>(devices), fromTime, toTime), HttpStatus.OK);
            }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }
}
