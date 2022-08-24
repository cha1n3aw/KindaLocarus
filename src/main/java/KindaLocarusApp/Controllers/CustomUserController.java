package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CustomUserController
{
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DeviceService deviceService;
    private final CustomUserService customUserService;

    /** Constructor based dependency injection */
    @Autowired
    public CustomUserController(DeviceService deviceService,
                                CustomUserService customUserService,
                                BCryptPasswordEncoder bCryptPasswordEncoder,
                                MongoTemplate mongoTemplate)
    {
        this.deviceService = deviceService;
        this.customUserService = customUserService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mongoTemplate = mongoTemplate;
    }
    /** TODO: access levels required */
    /** multiple parameters accepted, .../api/users.get?userId=1,2,3 OR .../api/users.get?userId=1&userId=2 */

    @PostMapping("/users.add")
    public ResponseEntity<Response<?>> AddUser(@RequestBody CustomUser customUser)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                if (customUserService.addUser(customUser)) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.OK.value()); setResponseData("OK");}}, HttpStatus.OK);
                else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); setResponseData("Internal server error");}}, HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseData("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseData("Unauthorized");}}, HttpStatus.OK);
    }

    /*@GetMapping("/users.add")
    @ResponseBody
    public ResponseEntity<Response<?>> AddUser(
            @RequestParam(required = true, name="name", defaultValue = "empty") String username,
            @RequestParam(required = true, name= "password", defaultValue = "empty") String password,
            @RequestParam(required = false, name="roles", defaultValue = "USER") List<String> roles,
            @RequestParam(required = false, name="devices", defaultValue = "0") List<String> devices,
            @RequestParam(required = false, name="desc", defaultValue = "") String description)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                if (customUserService.addUser(username, password, roles, devices, description)) return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.OK.value()); setResponseData("OK");}}, HttpStatus.OK);
                else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); setResponseData("Internal server error");}}, HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseData("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseData("Unauthorized");}}, HttpStatus.OK);
    }*/

    @GetMapping("/users.getCurrent")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            return customUserService.getUsers(new ArrayList<>()
            {
                {
                    add(authentication.getName());
                }
            });
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseData("Unauthorized");}}, HttpStatus.OK);
    }

    @GetMapping("/users.get")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers(
            @RequestParam(required = true, name="usernames", defaultValue = "0") List<String> usernames)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN"))) return customUserService.getUsers(usernames);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseData("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseData("Unauthorized");}}, HttpStatus.OK);
    }
}
