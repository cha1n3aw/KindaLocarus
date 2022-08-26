package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.KindaLocarusApp;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static KindaLocarusApp.Constants.Constants.USERNAME_FIELD;
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
                    customUser.setId(null);
                    customUser.setPassword(bCryptPasswordEncoder.encode(customUser.getPassword()));
                    mongoTemplate.indexOps("Users").ensureIndex(new Index(USERNAME_FIELD, Sort.Direction.DESC).unique());
                    mongoTemplate.save(customUser);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to add %s, reason: %s ", customUser.getUsername(), e.getMessage());
                }
            }
            if (errorsCount > 0)
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                errorDesc += String.format("Overall failed to add %s user(-s) ", errorsCount);
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
                    Query query = Query.query(Criteria.where(USERNAME_FIELD).is(username));
                    if (!mongoTemplate.exists(query, USERS_COLLECTION_NAME)) throw new Exception("Unable to locate such user! ");
                    mongoTemplate.findAndRemove(query, CustomUser.class);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to delete %s, reason: %s ", username, e.getMessage());
                }
            }
            if (errorsCount > 0)
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                errorDesc += String.format("Overall deleted %s, failed %s ", usernames.stream().count() - errorsCount, errorsCount);
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
                    customUserUpdates.setId(null);
                    /** TODO: unique USERNAME will fix those queries (get rid of 'em) */
                    Query query = Query.query(Criteria.where(USERNAME_FIELD).is(customUserUpdates.getUsername()));
                    CustomUser customUser = mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME);
                    if (customUser == null) throw new Exception("Unable to locate such user!");
                    /** TODO: migrate to an .update() method */
                    for (Field field : customUserUpdates.getClass().getDeclaredFields())
                    {
                        if(!(Objects.equals(field.getName(), "_id")))
                        {
                            Object fieldObject = customUserUpdates.getClass().getMethod("get" + StringUtils.capitalize(field.getName()), null).invoke(customUserUpdates);
                            if (fieldObject != null)
                            {
                                Class[] methodArgs = new Class[1];
                                if (Objects.equals(fieldObject.getClass(), HashSet.class)) methodArgs[0] = Object.class;
                                else methodArgs[0] = fieldObject.getClass();
                                customUser.getClass().getMethod("set" + StringUtils.capitalize(field.getName()), methodArgs).invoke(customUser, fieldObject);
                            }
                        }
                    }
                    customUserUpdates.setId(customUser.getId());
                    if (customUserUpdates.getPassword() != null && customUserUpdates.getPassword() != "") customUserUpdates.setPassword(bCryptPasswordEncoder.encode(customUserUpdates.getPassword()));
                    mongoTemplate.save(customUser);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to edit %s, reason: %s ", customUserUpdates.getUsername(), e.getMessage());
                }
            }
            if (errorsCount > 0)
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                errorDesc += String.format("Overall edited %s, failed %s ", partialUpdates.stream().count() - errorsCount, errorsCount);
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
            response.setResponseErrorDesc(String.format("Internal server error, reason: %s ", e.getMessage()));
        }
        return response;
    }

    public Response<?> getUsers(final List<String> usernames, final List<String> fields)
    {
        Response<HashSet> response = new Response<>();
        try
        {
            int userErrorsCount = 0;
            String errorDesc = "";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            HashSet<CustomUser> customUsers = new HashSet<>();
            for (String username : usernames)
            {
                try
                {
                    Query query = new Query();
                    query.addCriteria(Criteria.where(USERNAME_FIELD).is(username));
                    CustomUser customUser = mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME);
                    if (customUser == null) throw new Exception("Unable to locate such user!");
                    CustomUser customTempUser = new CustomUser();
                    if (fields != null)
                    {
                        int fieldErrorsCount = 0;
                        for (String field : fields)
                        {
                            try
                            {
                                Object fieldObject = customUser.getClass().getMethod("get" + StringUtils.capitalize(field), null).invoke(customUser);
                                Class[] methodArgs = new Class[1];
                                if (Objects.equals(fieldObject.getClass(), LinkedHashSet.class)) methodArgs[0] = Object.class;
                                else methodArgs[0] = fieldObject.getClass();
                                customTempUser.getClass().getMethod("set" + StringUtils.capitalize(field), methodArgs).invoke(customTempUser, fieldObject);
                            }
                            catch (Exception e)
                            {
                                fieldErrorsCount++;
                                errorDesc += String.format("Failed to get field %s for user %s, reason: %s ", field, username, e.getMessage());
                            }
                        }
                        if (fieldErrorsCount > 0)
                        {
                            errorDesc += String.format("Overall failed to get %s fields for user %s ", fieldErrorsCount, username);
                            response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                        }
                    }
                    else customTempUser = customUser;
                    customUsers.add(customTempUser);
                }
                catch (Exception e)
                {
                    userErrorsCount++;
                    errorDesc += String.format("Failed to get user %s, reason: %s ", username, e.getMessage());
                }
            }
            if (userErrorsCount > 0)
            {
                errorDesc += String.format("Overall failed to get %s users ", userErrorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(errorDesc += "OK");
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