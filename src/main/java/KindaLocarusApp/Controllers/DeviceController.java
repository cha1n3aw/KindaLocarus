package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Interfaces.Services.CustomUserService;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.USERNAME_FIELD;
import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

@RestController
@RequestMapping("/api")
public class DeviceController
{
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DeviceService deviceService;
    private final CustomUserService userService;

    @Autowired
    public DeviceController(DeviceService deviceService,
                            CustomUserService userService,
                            BCryptPasswordEncoder bCryptPasswordEncoder,
                            MongoTemplate mongoTemplate)
    {
        this.deviceService = deviceService;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/devices.getInfo")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers(
            @RequestParam(required = false, name="imeies") List<String> imeies,
            @RequestParam(required = false, name="fields") List<String> fields)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(deviceService.devicesGetInfo(imeies, fields), HttpStatus.OK);
            else
            {
                HashSet<String> devices = mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(authentication.getName())), CustomUser.class, USERS_COLLECTION_NAME).getDevices();
                devices.retainAll(imeies);
                return new ResponseEntity<>(deviceService.devicesGetInfo(new ArrayList<>(devices), fields), HttpStatus.OK);
            }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }
}
