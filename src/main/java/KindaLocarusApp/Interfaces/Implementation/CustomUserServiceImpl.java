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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

@Service
public class CustomUserServiceImpl implements CustomUserService
{
    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public CustomUserServiceImpl(
            BCryptPasswordEncoder bCryptPasswordEncoder,
            MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Response<?> addUsers(final List<CustomUser> customUsers)
    {
        Response response = new Response();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            for (CustomUser customUser : customUsers)
            {
                try
                {
                    customUser.setPassword(bCryptPasswordEncoder.encode(customUser.getPassword()));
                    mongoTemplate.insert(customUser);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to add %s, reason: %s\n", customUser.getUsername(), e.getMessage());
                }
            }
            if (errorsCount > 0)
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                errorDesc += String.format("Overall failed to add %s user(-s)", errorsCount);
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc("OK");
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: ", e.getMessage()));
        }
        return response;
    }

    public Response<?> deleteUsers(final List<String> usernames)
    {
        Response response = new Response();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            for (String username : usernames)
            {
                try
                {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("username").is(username));
                    mongoTemplate.remove(query, USERS_COLLECTION_NAME);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to delete %s, reason: %s\n", username, e.getMessage());
                }
            }
            if (errorsCount > 0)
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                errorDesc += String.format("Overall deleted %s, failed %s", usernames.stream().count() - errorsCount, errorsCount);
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc("OK");
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s", e.getMessage()));
        }
        return response;
    }

    public Response<?> editUsers(final List<CustomUser> partialUpdates)
    {
        /** TODO: implement EDIT CURRENT (ME, MYSELF) */
        Response response = new Response();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            for (CustomUser customUserUpdates : partialUpdates)
            {
                try
                {
                    /** TODO: unique USERNAME will fix those queries (get rid of 'em) */
                    Query query = new Query();
                    query.addCriteria(Criteria.where("username").is(customUserUpdates.getUsername()));
                    customUserUpdates.setId(mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME).getId());
                    if (customUserUpdates.getPassword() != null && customUserUpdates.getPassword() != "") customUserUpdates.setPassword(bCryptPasswordEncoder.encode(customUserUpdates.getPassword()));
                    mongoTemplate.save(customUserUpdates);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to edit %s, reason: %s\n", customUserUpdates.getUsername(), e.getMessage());
                }
            }
            if (errorsCount > 0)
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                errorDesc += String.format("Overall edited %s, failed %s", partialUpdates.stream().count() - errorsCount, errorsCount);
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc("OK");
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s", e.getMessage()));
        }
        return response;
    }

    public Response<?> getUsers(final List<String> usernames)
    {
        Response<String> response = new Response<>();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<CustomUser> customUsers = new HashSet<>();
            for (String username : usernames)
            {
                try
                {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("username").is(username));
                    customUsers.add(mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME));
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to get %s, reason: %s\n", username, e.getMessage());
                }
            }
            if (errorsCount > 0)
            {
                errorDesc += String.format("Overall failed to get %s users", errorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc("OK");
            }
            response.setResponseData(new Gson().toJson(customUsers));
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s", e.getMessage()));
        }
        return response;
    }
}