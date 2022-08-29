package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Interfaces.Services.CustomUserService;
import KindaLocarusApp.KindaLocarusApp;
import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Device;
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

import static KindaLocarusApp.Constants.Constants.*;

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

    public Response<?> usersAdd(final List<CustomUser> customUsers)
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
                    if (customUser.getPassword() != null && customUser.getPassword().length() >= 6 && customUser.getPassword().length() <=32)
                        customUser.setPassword(bCryptPasswordEncoder.encode(customUser.getPassword()));
                    else throw new Exception(String.format("Incorrect password length! "), new Throwable("PASSWORD_INCOMPATIBLE"));
                    customUser.setPassword(bCryptPasswordEncoder.encode(customUser.getPassword()));
                    mongoTemplate.indexOps(USERS_COLLECTION_NAME).ensureIndex(new Index(USERNAME_FIELD, Sort.Direction.DESC).unique());
                    mongoTemplate.save(customUser);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to add '%s', reason: '%s' : '%s' ", customUser.getUsername(), e.getMessage(), e.getCause());
                }
            }
            if (!Objects.equals(errorDesc, ""))
            {

                errorDesc += String.format("Overall failed to add '%s' user(-s) ", errorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: '%s' : '%s' ", e.getMessage(), e.getCause()));
        }
        return response;
    }

    public Response<?> usersDelete(final List<String> usernames)
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
                    if (!mongoTemplate.exists(query, USERS_COLLECTION_NAME)) throw new Exception(String.format("Unable to locate user '%s'! ", username), new Throwable("USER_NOTFOUND"));
                    mongoTemplate.findAndRemove(query, CustomUser.class);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to delete '%s', reason: '%s' : '%s' ", username, e.getMessage(), e.getCause());
                }
            }
            if (!Objects.equals(errorDesc, ""))
            {
                errorDesc += String.format("Overall deleted '%s', failed '%s' ", usernames.stream().count() - errorsCount, errorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: '%s' : '%s' ", e.getMessage(), e.getCause()));
        }
        return response;
    }

    public Response<?> usersEdit(final List<CustomUser> partialUpdates)
    {
        Response<?> response = new Response();
        try
        {
            int errorsCount = 0;
            String errorDesc = "";
            for (CustomUser customUserUpdates : partialUpdates)
            {
                try
                {
                    Query query = Query.query(Criteria.where(USERNAME_FIELD).is(customUserUpdates.getUsername()));
                    CustomUser customUser = mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME);
                    if (customUser == null) throw new Exception(String.format("Unable to locate user '%s'! ", customUserUpdates.getUsername()), new Throwable("USER_NOTFOUND"));
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
                    if (customUserUpdates.getPassword() != null && customUserUpdates.getPassword().length() >= 6 && customUserUpdates.getPassword().length() <=32)
                        customUser.setPassword(bCryptPasswordEncoder.encode(customUserUpdates.getPassword()));
                    else throw new Exception(String.format("Incorrect password length! "), new Throwable("PASSWORD_INCOMPATIBLE"));
                    mongoTemplate.save(customUser);
                }
                catch (Exception e)
                {
                    errorsCount++;
                    errorDesc += String.format("Failed to edit '%s', reason: '%s' : '%s' ", customUserUpdates.getUsername(), e.getMessage(), e.getCause());
                }
            }
            if (!Objects.equals(errorDesc, ""))
            {
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                errorDesc += String.format("Overall edited '%s', failed '%s' ", partialUpdates.stream().count() - errorsCount, errorsCount);
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
        }
        catch(Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: '%s' : '%s' ", e.getMessage(), e.getCause()));
        }
        return response;
    }

    public Response<?> usersGet(final List<String> usernames, final List<String> fields)
    {
        Response<HashSet> response = new Response<>();
        try
        {
            int userErrorsCount = 0, totalFieldsErrorCount = 0;
            String errorDesc = "";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            HashSet<CustomUser> customUsers = new HashSet<>();
            if (usernames == null)
            {
                customUsers.addAll(mongoTemplate.findAll(CustomUser.class, USERS_COLLECTION_NAME));
                for (CustomUser customUser : customUsers) customUser.setPassword(null);
            }
            else
            {
                for (String username : usernames)
                {
                    try
                    {
                        CustomUser customUser = mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(username)), CustomUser.class, USERS_COLLECTION_NAME);
                        customUser.setPassword(null);
                        if (customUser == null) throw new Exception(String.format("Unable to locate user '%s'! ", username), new Throwable("USER_NOTFOUND"));
                        CustomUser customTempUser = new CustomUser();
                        int fieldErrorsCount = 0;
                        if (fields != null)
                        {
                            for (String field : fields)
                            {
                                try
                                {
                                    if (Objects.equals(field, "password")) throw new Exception("Password cannot be accessed", new Throwable("ACCESS_DENIED"));
                                    Object fieldObject = customUser.getClass().getMethod("get" + StringUtils.capitalize(field), null).invoke(customUser);
                                    Class[] methodArgs = new Class[1];
                                    if (Objects.equals(fieldObject.getClass(), LinkedHashSet.class)) methodArgs[0] = Object.class;
                                    else methodArgs[0] = fieldObject.getClass();
                                    customTempUser.getClass().getMethod("set" + StringUtils.capitalize(field), methodArgs).invoke(customTempUser, fieldObject);
                                }
                                catch (Exception e)
                                {
                                    fieldErrorsCount++;
                                    errorDesc += String.format("Failed to get field '%s' for user '%s', reason: '%s' : '%s' ", field, username, e.getMessage(), e.getCause());
                                }
                            }
                            if (fieldErrorsCount > 0) totalFieldsErrorCount+=fieldErrorsCount;
                        }
                        else customTempUser = customUser;
                        if (customTempUser != null && (fields == null || fieldErrorsCount != fields.stream().count())) customUsers.add(customTempUser);
                    }
                    catch (Exception e)
                    {
                        userErrorsCount++;
                        errorDesc += String.format("Failed to get user '%s', reason: '%s' : '%s' ", username, e.getMessage(), e.getCause());
                    }
                }
            }
            if (!Objects.equals(errorDesc, ""))
            {
                errorDesc += String.format("Overall failed to get '%s' fields for '%s' users ", totalFieldsErrorCount, userErrorsCount);
                response.setResponseStatus(HttpStatus.EXPECTATION_FAILED.value());
                response.setResponseErrorDesc(errorDesc);
            }
            else
            {
                response.setResponseStatus(HttpStatus.OK.value());
                response.setResponseErrorDesc(null);
            }
            if (customUsers != null && customUsers.stream().count() > 0) response.setResponseData(customUsers);
            else response.setResponseData(null);
        }
        catch (Exception e)
        {
            response.setResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseErrorDesc(String.format("Internal server error, reason: '%s' : '%s' ", e.getMessage(), e.getCause()));
        }
        return response;
    }
}