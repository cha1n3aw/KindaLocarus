package KindaLocarusApp.Controllers;

import KindaLocarusApp.Interfaces.Services.DeviceService;
import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CustomUserController
{
    private final CustomUserService customUserService;

    @Autowired
    public CustomUserController(
            CustomUserService customUserService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            MongoTemplate mongoTemplate)
    {
        this.customUserService = customUserService;
        try
        {
            if (!mongoTemplate.collectionExists("Users")) mongoTemplate.createCollection(CustomUser.class);
            if (mongoTemplate.getCollection("Users").countDocuments() == 0)
            {
                CustomUser admin = new CustomUser();
                HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(){{
                    add(new SimpleGrantedAuthority("SUPERADMIN"));
                    add(new SimpleGrantedAuthority("ADMIN"));
                    add(new SimpleGrantedAuthority("USER"));
                }};
                admin.setUsername("admin");
                admin.setPassword(bCryptPasswordEncoder.encode("admin"));
                admin.setAuthorities(grantedAuthorities);
                admin.setDescription("This is Admin account");
                admin.setAccountEnabled(true);
                admin.setAccountNonExpired(true);
                admin.setAccountNonLocked(true);
                admin.setCredentialsNonExpired(true);
                admin.setDevices(new HashSet<>(){{add("ALL");}});
                mongoTemplate.insert(admin);
            }
        }
        catch (Exception e)
        {

        }
    }
    /** multiple parameters accepted, .../api/users.get?userId=1,2,3 OR .../api/users.get?userId=1&userId=2 */

    @PostMapping("/users.add")
    public ResponseEntity<Response<?>> AddUsers(@RequestBody List<CustomUser> customUsers)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(customUserService.addUsers(customUsers), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @PatchMapping("/users.edit")
    public ResponseEntity<Response<?>> EditUsers(@RequestBody List<CustomUser> partialUpdates)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(customUserService.editUsers(partialUpdates), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @DeleteMapping("/users.delete")
    public ResponseEntity<Response<?>> DeleteUsers(@RequestParam(required = true, name="usernames", defaultValue = "") List<String> usernames)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(customUserService.deleteUsers(usernames), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @GetMapping("/users.getCurrent")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers(@RequestParam(required = false, name="fields") List<String> fields)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            return new ResponseEntity<>(customUserService.getUsers(new ArrayList<>(){{add(authentication.getName());}}, fields), HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @PatchMapping("/users.editCurrent")
    public ResponseEntity<Response<?>> EditUsers(@RequestBody CustomUser partialUpdate)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            return new ResponseEntity<>(customUserService.editUsers(new ArrayList<>(){{add(partialUpdate);}}), HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @GetMapping("/users.get")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers(
            @RequestParam(required = true, name="usernames") List<String> usernames,
            @RequestParam(required = false, name="fields") List<String> fields)
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                if (usernames.stream().count() != 0)
                    return new ResponseEntity<>(customUserService.getUsers(usernames, fields), HttpStatus.OK);
                else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.BAD_REQUEST.value()); setResponseErrorDesc("Bad request");}}, HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }
}
