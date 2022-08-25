package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.KindaLocarusApp;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
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
        Response<?> response = new Response();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            for (CustomUser customUser : customUsers)
            {
                try
                {
                    /** TODO: test users.add method (correct id is inserted automatically?) */
                    customUser.setId(null);
                    customUser.setPassword(bCryptPasswordEncoder.encode(customUser.getPassword()));
                    Query query = new Query();
                    query.addCriteria(Criteria.where("username").is(customUser.getUsername()));
                    if (mongoTemplate.exists(query, USERS_COLLECTION_NAME)) throw new Exception("User already exists!");
                    else mongoTemplate.insert(customUser);
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
        Response<?> response = new Response();
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
                    if (!mongoTemplate.remove(query, USERS_COLLECTION_NAME).wasAcknowledged()) throw new Exception("Unable to locate such user!");
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
        Response<?> response = new Response();
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
                    CustomUser customUser = mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME);
                    if (customUser == null) throw new Exception("Unable to locate such user!");
                    customUserUpdates.setId(customUser.getId());
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

    public Response<?> getUsers(final List<String> usernames, final List<String> fields)
    {
        Response<Set> response = new Response<>();
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
                    CustomUser customUser = mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME);
                    if (customUser == null) throw new Exception("Unable to locate such user!");
                    CustomUser customNewUser = new CustomUser();
                    /** TODO: TEST FIELDS (they're very weird) */
                    for (String field : fields)
                    {
                        Method getField = customUser.getClass().getMethod("get" + StringUtils.capitalize(field));
                        Object fieldObject = getField.invoke(customUser);
                        Class[] methodArgs = new Class[1];
                        methodArgs[0] = (customUser.getClass().getField(field)).getClass();
                        Method setField = customNewUser.getClass().getMethod("set" + StringUtils.capitalize(field), methodArgs);
                        setField.invoke(customUser, fieldObject);
                    }
                    customUsers.add(customNewUser);
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
            response.setResponseData(customUsers);
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s : %s", e.getMessage(), e.getCause()));
        }
        return response;
    }
}