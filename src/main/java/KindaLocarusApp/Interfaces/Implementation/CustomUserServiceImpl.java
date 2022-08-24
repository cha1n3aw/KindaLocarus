package KindaLocarusApp.Interfaces.Implementation.Users;

import KindaLocarusApp.Interfaces.Services.Users.CustomUserService;
import KindaLocarusApp.Interfaces.Services.Users.Models.CustomUser;
import KindaLocarusApp.Interfaces.Services.Users.Models.Response;
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

    public boolean addUser(final String username, final String password, final List<String> roles)
    {
        try
        {
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            for (String role : roles) grantedAuthorities.add(new SimpleGrantedAuthority(role));
            CustomUser user = new CustomUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setAuthorities(grantedAuthorities);
            mongoTemplate.save(user);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public ResponseEntity<Response<?>> getUsers(final List<String> usernames)
    {
        Response<String> response = new Response<>();
        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null)
            {
                for (String username : usernames)
                {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("username").is(username));
                    response.setResponseData(new Gson().toJson(mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME)));
                    response.setResponseStatus(Integer.valueOf(HttpStatus.OK.value()));
                }
            }
            else throw new Exception();
        }
        catch (Exception e)
        {
            response.setResponseStatus(Integer.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            response.setResponseData("Internal server error");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}