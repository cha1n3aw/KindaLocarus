package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Repositories.Users.CustomUserRepo;
import KindaLocarusApp.Interfaces.Services.API.DeviceService;
import KindaLocarusApp.Interfaces.Services.Users.CustomUserService;
import KindaLocarusApp.Models.API.Response;
import KindaLocarusApp.Models.Users.CustomUser;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.*;

@RestController
@RequestMapping("/api")
public class CustomUserController
{
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    //private final CustomUserRepo customUserRepo;
    private final DeviceService deviceService;
    private final CustomUserService userService;

    /** Constructor based dependency injection */
    @Autowired
    public CustomUserController(DeviceService deviceService,
                                CustomUserService userService,
                                //CustomUserRepo customUserRepo,
                                BCryptPasswordEncoder bCryptPasswordEncoder,
                                MongoTemplate mongoTemplate)
    {
        this.deviceService = deviceService;
        this.userService = userService;
        //this.customUserRepo = customUserRepo;
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
        {
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
            {
                Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                for (String role : roles) grantedAuthorities.add(new SimpleGrantedAuthority(role));
                CustomUser user = new CustomUser();
                user.setUsername(username);
                user.setPassword(password);
                user.setAuthorities(grantedAuthorities);
                mongoTemplate.save(user);
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
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is("pivAS"));
        users = mongoTemplate.find(query, KindaLocarusApp.Models.Users.CustomUser.class, USERS_COLLECTION_NAME);
        return ((KindaLocarusApp.Models.Users.CustomUser)(users.toArray()[0])).getUsername();
    }

    @GetMapping("/ssa")
    @ResponseBody
    public ResponseEntity<Response<?>> Blyad()
    {
        Response<String> response = new Response<>();
        response.setResponseData("ADDED");
        response.setResponseStatus(REQUEST_SUCCESS);
        CustomUser user = new CustomUser();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
        user.setUsername("PHONKYYY");
        user.setPassword(bCryptPasswordEncoder.encode("HONKY"));
        user.setAuthorities(grantedAuthorities);
        mongoTemplate.save(user, USERS_COLLECTION_NAME);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users.getCurrent")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers() throws JsonProcessingException
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Response<String> response = new Response<>();
        if (authentication != null)
        {
            response.setResponseStatus(REQUEST_SUCCESS);
            String str = "";
            Query query = new Query();
            query.addCriteria(Criteria.where("username").is(authentication.getName()));
            str += mongoTemplate.findOne(query, KindaLocarusApp.Models.Users.CustomUser.class, USERS_COLLECTION_NAME).getUsername();
            for (GrantedAuthority b : mongoTemplate.findOne(query, KindaLocarusApp.Models.Users.CustomUser.class, USERS_COLLECTION_NAME).getAuthorities()) str += b.getAuthority();
            //if (auth.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN"))) str += "_GOT";

            response.setResponseData(new Gson().toJson(mongoTemplate.findOne(query, KindaLocarusApp.Models.Users.CustomUser.class, USERS_COLLECTION_NAME)));
            System.out.println(new Gson().toJson(mongoTemplate.findOne(query, KindaLocarusApp.Models.Users.CustomUser.class, USERS_COLLECTION_NAME)));
            //Gson gson = new Gson();
            //String json = gson.toJson(user);
            //response.setResponseData(new ObjectMapper().writeValueAsString(mongoTemplate.findOne(query, KindaLocarusApp.Models.Users.CustomUser.class, USERS_COLLECTION_NAME)));

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
