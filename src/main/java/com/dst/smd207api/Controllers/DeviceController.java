package com.dst.smd207api.Controllers;

import com.dst.smd207api.Interfaces.Services.DeviceService;
import com.dst.smd207api.Models.Device;
import com.dst.smd207api.Models.Response;
import com.dst.smd207api.Interfaces.Services.CustomUserService;
import com.dst.smd207api.Models.CustomUser;
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
import java.util.List;

import static com.dst.smd207api.Constants.Constants.*;

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
            if (imeis == null || imeis.stream().count() == 0) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect IMEIs: at least one correct IMEI is required");}}, HttpStatus.OK);
            if (fields == null || fields.stream().count() == 0) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect fields: at least one correct field is required");}}, HttpStatus.OK);
            List<Long> devices = new ArrayList<>();
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
            {
                if (imeis.contains("all")) for (Device device : mongoTemplate.findAll(Device.class, DEVICES_COLLECTION_NAME)) devices.add(device.getDeviceImei());
                else
                    for (String dev : imeis)
                        try
                        {
                            long tempDev = Long.parseLong(dev);
                            if (tempDev < 100000000000000L || tempDev > 999999999999999L) throw new Exception();
                            else devices.add(tempDev);
                        }
                        catch (Exception e) { }
            }
            else
            {
                devices = new ArrayList<>(mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(authentication.getName())), CustomUser.class, USERS_COLLECTION_NAME).getDevices());
                if (!imeis.contains("all"))
                {
                    List<Long> longImeis = new ArrayList<>();
                    for (String dev : imeis)
                        try
                        {
                            long tempDev = Long.parseLong(dev);
                            if (tempDev < 100000000000000L || tempDev > 999999999999999L) throw new Exception();
                            else longImeis.add(tempDev);
                        }
                        catch (Exception e) { }
                    devices.retainAll(longImeis);
                }
            }
            if (devices == null || devices.stream().count() == 0 ) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect IMEIs: at least one correct IMEI is required");}}, HttpStatus.OK);
            else return new ResponseEntity<>(deviceService.devicesGet(devices, fields), HttpStatus.OK);
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
    public ResponseEntity<Response<?>> DevicesDelete(@RequestParam(required = true, name="imeis") List<String> objectImeis)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
        {
            List<Long> imeis = new ArrayList<>();
            for (String imei : objectImeis)
                try
                {
                    long tempImei = Long.parseLong(imei);
                    if (tempImei < 100000000000000L || tempImei > 999999999999999L) throw new Exception();
                    else imeis.add(tempImei);
                }
                catch (Exception e) { }
            if (imeis == null || imeis.stream().count() == 0) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect IMEIs: at least one correct IMEI is required");}}, HttpStatus.OK);
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(deviceService.devicesDelete(imeis), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>()
            {{
                setResponseStatus(HttpStatus.FORBIDDEN.value());
                setResponseErrorDesc("Forbidden");
            }}, HttpStatus.OK);
        }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @GetMapping("/devices.prolongLicense")
    @ResponseBody
    public ResponseEntity<Response<?>> DevicesProlongLicense(
            @RequestParam(required = true, name="imeis", defaultValue = "") List<String> objectImeis,
            @RequestParam(required = true, name="issueDate", defaultValue = "") String objectIssueDate,
            @RequestParam(required = true, name="expirationDate", defaultValue = "") String objectExpirationDate)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null)
        {
            List<Long> imeis = new ArrayList<>();
            for (String imei : objectImeis)
                try
                {
                    long tempImei = Long.parseLong(imei);
                    if (tempImei < 100000000000000L || tempImei > 999999999999999L) throw new Exception();
                    else imeis.add(tempImei);
                }
                catch (Exception e) { }
            if (imeis == null || imeis.stream().count() == 0) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect IMEIs: at least one correct IMEI is required");}}, HttpStatus.OK);
            Instant issueDate;
            Instant expirationDate;
            try
            {
                issueDate = Instant.parse(objectIssueDate);
                expirationDate = Instant.parse(objectExpirationDate);
            }
            catch(Exception e)
            {
                return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect timestamps: format should be YYYY-MM-DDT00:00:00Z");}}, HttpStatus.OK);
            }
            if (issueDate.isAfter(expirationDate)) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Incorrect timestamps: 'Issue date' should precede 'Expiration date'");}}, HttpStatus.OK);
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN"))) return new ResponseEntity<>(deviceService.devicesProlongLicense(imeis, issueDate, expirationDate), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }
}
