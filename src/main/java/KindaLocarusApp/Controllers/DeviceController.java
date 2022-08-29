package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Device;
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

import static KindaLocarusApp.Constants.Constants.*;

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

    @GetMapping("/devices.get")
    @ResponseBody
    public ResponseEntity<Response<?>> DevicesGet(
            @RequestParam(required = true, name="imeis", defaultValue = "") List<String> imeis,
            @RequestParam(required = true, name="fields", defaultValue = "") List<String> fields)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
        {
            if (imeis == null || fields == null || imeis.stream().count() == 0 || fields.stream().count() == 0) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("IMEIs and fields are required");}}, HttpStatus.OK);
            List<String> devices = new ArrayList<>();
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
            {
                if (imeis.contains("all")) for (Device device : mongoTemplate.findAll(Device.class, DEVICES_COLLECTION_NAME)) devices.add(device.getDeviceImei());
                else devices = imeis;
            }
            else
            {
                devices = new ArrayList<>(mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(authentication.getName())), CustomUser.class, USERS_COLLECTION_NAME).getDevices());
                if (!imeis.contains("all")) devices.retainAll(imeis);
            }
            return new ResponseEntity<>(deviceService.devicesGet(devices, fields), HttpStatus.OK);
        }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @PostMapping("/devices.add")
    public ResponseEntity<Response<?>> DevicesAdd(@RequestBody List<Device> devices)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN"))) return new ResponseEntity<>(deviceService.devicesAdd(devices), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @PatchMapping("/devices.edit")
    public ResponseEntity<Response<?>> DevicesEdit(@RequestBody List<Device> partialUpdates)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN"))) return new ResponseEntity<>(deviceService.devicesEdit(partialUpdates), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @DeleteMapping("/devices.delete")
    public ResponseEntity<Response<?>> DevicesDelete(@RequestParam(required = true, name="imeis") List<String> imeis)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN"))) return new ResponseEntity<>(deviceService.devicesDelete(imeis), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }
}
