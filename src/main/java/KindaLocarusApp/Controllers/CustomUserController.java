package KindaLocarusApp.Controllers;

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

import static KindaLocarusApp.Constants.Constants.*;

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
            if (!mongoTemplate.collectionExists(USERS_COLLECTION_NAME)) mongoTemplate.createCollection(CustomUser.class);
            if (mongoTemplate.getCollection(USERS_COLLECTION_NAME).countDocuments() == 0)
            {
                CustomUser admin = new CustomUser();
                HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(){{
                    add(new SimpleGrantedAuthority(SUPERADMIN_ROLE));
                    add(new SimpleGrantedAuthority(ADMIN_ROLE));
                    add(new SimpleGrantedAuthority(USER_ROLE));
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
    public ResponseEntity<Response<?>> UsersAdd(@RequestBody List<CustomUser> customUsers)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities() != null && authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(customUserService.usersAdd(customUsers), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @PatchMapping("/users.edit")
    public ResponseEntity<Response<?>> UsersEdit(@RequestBody List<CustomUser> partialUpdates)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities() != null && authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(customUserService.usersEdit(partialUpdates), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @DeleteMapping("/users.delete")
    public ResponseEntity<Response<?>> UsersDelete(@RequestParam(required = true, name="usernames") List<String> usernames)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities() != null && authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(customUserService.usersDelete(usernames), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @GetMapping("/users.getCurrent")
    @ResponseBody
    public ResponseEntity<Response<?>> UsersGet(@RequestParam(required = false, name="fields") List<String> fields)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            return new ResponseEntity<>(customUserService.usersGet(new ArrayList<>(){{add(authentication.getName());}}, fields), HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @PatchMapping("/users.editCurrent")
    public ResponseEntity<Response<?>> EditUsers(@RequestBody CustomUser partialUpdate)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            return new ResponseEntity<>(customUserService.usersEdit(new ArrayList<>(){{add(partialUpdate);}}), HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }

    @GetMapping("/users.get")
    @ResponseBody
    public ResponseEntity<Response<?>> GetUsers(
            @RequestParam(required = false, name="usernames") List<String> usernames,
            @RequestParam(required = false, name="fields") List<String> fields)
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken))
            if (authentication.getAuthorities() != null && authentication.getAuthorities().stream().anyMatch(c -> c.getAuthority().equals("ADMIN")))
                return new ResponseEntity<>(customUserService.usersGet(usernames, fields), HttpStatus.OK);
            else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Forbidden");}}, HttpStatus.OK);
        else return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.UNAUTHORIZED.value()); setResponseErrorDesc("Unauthorized");}}, HttpStatus.OK);
    }
}
