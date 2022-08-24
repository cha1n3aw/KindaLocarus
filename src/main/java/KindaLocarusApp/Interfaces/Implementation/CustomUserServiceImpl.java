package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;
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
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

@Service
public class CustomUserServiceImpl implements CustomUserService
{
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomUserServiceImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    public String addUser(final List<CustomUser> customUsers)
    {
        try
        {
            /** TODO: detailed error output: which users failed */
            int existingUsersCount = 0;
            for (CustomUser customUser : customUsers)
            {
                Query query = new Query();
                query.addCriteria(Criteria.where("username").is(customUser.getUsername()));
                if (!mongoTemplate.exists(query, USERS_COLLECTION_NAME)) mongoTemplate.insert(customUser);
                else existingUsersCount++;
            }
            if (existingUsersCount > 0) return String.format("Failed to add %s user(-s), reason: user already exists", existingUsersCount);
            else return "OK";
        }
        catch(Exception e)
        {
            return String.format("Failed to add %s user(-s), reason: Internal server error", customUsers.stream().count());
        }
    }

    /*public boolean addUser(final String username, final String password, final List<String> roles, final List<String> devices, final String description)
    {
        try
        {
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            for (String role : roles) grantedAuthorities.add(new SimpleGrantedAuthority(role));
            Set<String> ownedDevices = new HashSet<>(devices);
            CustomUser user = new CustomUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setAuthorities(grantedAuthorities);
            user.setOwnedDevices(ownedDevices);
            user.setUserDescription(description);
            mongoTemplate.insert(user);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }*/

    public ResponseEntity<Response<?>> getUsers(final List<String> usernames)
    {
        Response<String> response = new Response<>();
        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null)
            {
                Set<CustomUser> customUsers = new HashSet<>();
                for (String username : usernames)
                {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("username").is(username));
                    customUsers.add(mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME));
                }
                response.setResponseData(new Gson().toJson(customUsers));
                response.setResponseStatus(HttpStatus.OK.value());
            }
            else throw new Exception();
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseData("Internal server error");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}