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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

public class DeviceController
{
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DeviceService deviceService;
    private final CustomUserService userService;

    /** Constructor based dependency injection */
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

    /** multiple parameters accepted, .../api/devices.get?imei=1,2,3
     * parameters all and active are not required and extend to a 0 and 1 unless specified
     * this helps to shorten an api request */
//    @GetMapping("/devices.get")
//    @ResponseBody
//    public ResponseEntity<Response<?>> GetDevices(
//            @RequestParam(required = false, name="imei") List<String> imeies,
//            @RequestParam(required = false, name="all", defaultValue = "0") Boolean returnAll,
//            @RequestParam(required = false, name="active", defaultValue = "1") Boolean returnActive)
//    {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Query query = new Query();
//        query.addCriteria(Criteria.where("username").is(authentication.getName()));
//        CustomUser user = mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME);
//        Set<String> devices = user.getDevices();
//        devices.retainAll(imeies);
//        return new ResponseEntity<>(deviceService.getDevices(new ArrayList<>(devices), returnAll, returnActive), HttpStatus.OK);
//    }
}
