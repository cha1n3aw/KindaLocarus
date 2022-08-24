package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Interfaces.Services.CustomUserService;
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


    @GetMapping("/users.add")
    @ResponseBody
    public ResponseEntity<Response<?>> AddUser(
            @RequestParam(required = true, name="name", defaultValue = "empty") String username,
            @RequestParam(required = true, name= "password", defaultValue = "empty") String password,
            @RequestParam(required = false, name="roles", defaultValue = "USER") List<String> roles)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                if (customUserService.addUser(username, password, roles)) return new ResponseEntity<>(new Response<>(){{setResponseStatus(Integer.valueOf(HttpStatus.OK.value())); setResponseData("OK");}}, HttpStatus.OK);
                else return new ResponseEntity<>(new Response<>(){{setResponseStatus(Integer.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())); setResponseData("Internal server error");}}, HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(Integer.valueOf(HttpStatus.FORBIDDEN.value())); setResponseData("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(Integer.valueOf(HttpStatus.UNAUTHORIZED.value())); setResponseData("Unauthorized");}}, HttpStatus.OK);
    }
/*

    @GetMapping("/ass")
    @ResponseBody
    public String Govno()
    {
        List<KindaLocarusApp.Models.CustomUser> users;
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is("pivAS"));
        users = mongoTemplate.find(query, KindaLocarusApp.Models.CustomUser.class, USERS_COLLECTION_NAME);
        return ((KindaLocarusApp.Models.CustomUser)(users.toArray()[0])).getUsername();
    }

    @GetMapping("/ssa")
    @ResponseBody
    public ResponseEntity<Response<?>> Blyad()
    {
        Response<String> response = new Response<>();
        response.setResponseData("ADDED");
        response.setResponseStatus(Integer.valueOf(HttpStatus.OK.value()));
        CustomUser user = new CustomUser();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
        user.setUsername("PHONKYYY");
        user.setPassword(bCryptPasswordEncoder.encode("HONKY"));
        user.setAuthorities(grantedAuthorities);
        mongoTemplate.save(user, USERS_COLLECTION_NAME);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
*/

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
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(Integer.valueOf(HttpStatus.UNAUTHORIZED.value())); setResponseData("Unauthorized");}}, HttpStatus.OK);
    }

    @GetMapping("/users.get")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers(
            @RequestParam(required = true, name="usernames", defaultValue = "0") List<String> usernames)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN"))) return customUserService.getUsers(usernames);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(Integer.valueOf(HttpStatus.FORBIDDEN.value())); setResponseData("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(Integer.valueOf(HttpStatus.UNAUTHORIZED.value())); setResponseData("Unauthorized");}}, HttpStatus.OK);
    }
}
