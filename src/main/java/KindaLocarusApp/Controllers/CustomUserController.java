package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Repositories.Users.CustomUserRepo;
import KindaLocarusApp.Interfaces.Services.API.DeviceService;
import KindaLocarusApp.Interfaces.Services.Users.CustomUserService;
import KindaLocarusApp.Models.API.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static KindaLocarusApp.Constants.Constants.REQUEST_ERROR;
import static KindaLocarusApp.Constants.Constants.REQUEST_SUCCESS;

@RestController
@RequestMapping("/api")
public class CustomUserController
{
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomUserRepo extendedUserDetailsRepo;
    private final DeviceService deviceService;
    private final CustomUserService userService;

    /** Constructor based dependency injection */
    @Autowired
    public CustomUserController(DeviceService deviceService,
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
    /** TODO: access levels required */
    /** multiple parameters accepted, .../api/users.get?userId=1,2,3 OR .../api/users.get?userId=1&userId=2 */


    @GetMapping("/users.add")
    @ResponseBody
    public ResponseEntity<Response<?>> AddUser(
            @RequestParam(required = true, name="name", defaultValue = "empty") String username,
            @RequestParam(required = true, name="pass", defaultValue = "empty") String userPassword,
            @RequestParam(required = false, name="roles", defaultValue = "USER") List<String> roles)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
        {
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
            {
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                for (String role : roles) grantedAuthorities.add(new SimpleGrantedAuthority(role));
                extendedUserDetailsRepo.save(new KindaLocarusApp.Models.Users.CustomUser(){{
                    setUsername(username);
                    setPassword(bCryptPasswordEncoder.encode(userPassword));
                    setAuthorities(grantedAuthorities);
                }});
                return new ResponseEntity<>(new Response<>(){{setResponseStatus(REQUEST_SUCCESS); setResponseData("ADDED SUCCESSFULLY");}}, HttpStatus.OK);
            }
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(REQUEST_ERROR); setResponseData("FORBIDDEN");}}, HttpStatus.FORBIDDEN);
        }
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(REQUEST_ERROR); setResponseData("UNAUTHORIZED");}}, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/ass")
    @ResponseBody
    public String Govno()
    {
        List<KindaLocarusApp.Models.Users.CustomUser> users;
        //ExtendedUserDetails user = extendedUserDetailsRepo.findExtendedUserDetailsByUsername("pivo");
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is("pivAS"));
        users = mongoTemplate.find(query, KindaLocarusApp.Models.Users.CustomUser.class, "ExtendedUserDetails");
        return ((KindaLocarusApp.Models.Users.CustomUser)(users.toArray()[0])).getUsername();
    }

    @GetMapping("/ssa")
    @ResponseBody
    public ResponseEntity<Response<?>> Blyad()
    {
        Response<String> response = new Response<>();
        response.setResponseData("VVV");
        response.setResponseStatus(REQUEST_SUCCESS);
        extendedUserDetailsRepo.save(new KindaLocarusApp.Models.Users.CustomUser(){{setUsername("pivo"); setPassword(bCryptPasswordEncoder.encode("vino")); setAuthorities(new ArrayList<GrantedAuthority>(){{add(new SimpleGrantedAuthority("USER")); add(new SimpleGrantedAuthority("ADMIN"));}});}});
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users.getCurrent")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Response<String> response = new Response<>();
        if (authentication != null)
        {
            //userRepo.save(new ExtendedUser(){{setUserName("igor"); setPassword(bCryptPasswordEncoder.encode("abc")); setGrantedAuthorities(new HashSet<GrantedAuthority>(){{add(new SimpleGrantedAuthority("ADMIN"));}});}});
            /** TODO: migrate to an extended user, fix casting, add userId, maybe UserDetails? */

            response.setResponseStatus(REQUEST_SUCCESS);
            String str = "";
            //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            str += extendedUserDetailsRepo.findExtendedUserDetailsByUsername(authentication.getName()).getUsername();
            for (GrantedAuthority b : extendedUserDetailsRepo.findExtendedUserDetailsByUsername(authentication.getName()).getAuthorities()) str += b.getAuthority();
            //if (auth.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN"))) str += "_GOT";
            response.setResponseData(str);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else
        {
            response.setResponseStatus(REQUEST_ERROR);
            response.setResponseData("UNAUTHORIZED");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/users.get")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers(
            @RequestParam(required = true, name="usernames", defaultValue = "0") List<String> usernames)
    {
        return userService.getUsers(usernames);
    }
}
