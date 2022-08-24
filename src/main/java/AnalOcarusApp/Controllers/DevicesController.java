package AnalOcarusApp.Controllers;

import AnalOcarusApp.Interfaces.Repositories.Users.CustomUserRepo;
import AnalOcarusApp.Interfaces.Services.API.DeviceService;
import AnalOcarusApp.Interfaces.Services.Users.CustomUserService;
import AnalOcarusApp.Models.API.Response;
import AnalOcarusApp.Models.Users.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DevicesController
{
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomUserRepo extendedUserDetailsRepo;
    private final DeviceService deviceService;
    private final CustomUserService userService;

    /** Constructor based dependency injection */
    @Autowired
    public DevicesController(DeviceService deviceService,
                               CustomUserService userService,
                               CustomUserRepo extendedUserDetailsRepo,
                               BCryptPasswordEncoder bCryptPasswordEncoder,
                               MongoTemplate mongoTemplate)
    {
        this.deviceService = deviceService;
        this.userService = userService;
        this.extendedUserDetailsRepo = extendedUserDetailsRepo;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mongoTemplate = mongoTemplate;
    }

    /** multiple parameters accepted, .../api/devices.get?imei=1,2,3
     * parameters all and active are not required and extend to a 0 and 1 unless specified
     * this helps to shorten an api request */
    @GetMapping("/devices.get")
    @ResponseBody
    public ResponseEntity<Response<?>> GetDevices(
            @RequestParam(required = false, name="imei") List<String> imeies,
            @RequestParam(required = false, name="all", defaultValue = "0") Boolean returnAll,
            @RequestParam(required = false, name="active", defaultValue = "1") Boolean returnActive)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser user = extendedUserDetailsRepo.findExtendedUserDetailsByUsername(authentication.getName());
        Set<String> devices = null;//user.getOwnedDevices();
        devices.retainAll(imeies);
        return deviceService.getDevices(new ArrayList<>(devices), returnAll, returnActive);
    }
}
