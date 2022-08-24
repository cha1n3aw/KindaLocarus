package KindaLocarusApp.Interfaces.Implementation.Users;

import KindaLocarusApp.Interfaces.Repositories.Users.CustomUserRepo;
import KindaLocarusApp.Interfaces.Services.Users.CustomUserService;
import KindaLocarusApp.Models.Users.CustomUser;
import KindaLocarusApp.Models.API.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static KindaLocarusApp.Constants.Constants.REQUEST_SUCCESS;
import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

@Service
public class CustomUserServiceImpl implements CustomUserService
{
    //private final CustomUserRepo userRepo;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomUserServiceImpl(//CustomUserRepo userRepo,
                                MongoTemplate mongoTemplate)
    {
        //this.userRepo = userRepo;
        this.mongoTemplate = mongoTemplate;
    }

    public ResponseEntity<Response<?>> getUsers(final List<String> usernames)
    {
        Response<String> a = new Response<>();
        a.setResponseStatus(REQUEST_SUCCESS);
        String usersResponse = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
        {
            for (String username : usernames)
            {
                Query query = new Query();
                query.addCriteria(Criteria.where("username").is(authentication.getName()));
                CustomUser user = mongoTemplate.findOne(query, KindaLocarusApp.Models.Users.CustomUser.class, USERS_COLLECTION_NAME);
                usersResponse += user.getUsername();
                usersResponse += "=";
                for (GrantedAuthority grantedAuthority : mongoTemplate.findOne(query, KindaLocarusApp.Models.Users.CustomUser.class, USERS_COLLECTION_NAME).getAuthorities())
                    usersResponse += (grantedAuthority.getAuthority() + " ");
            }
        }
        a.setResponseData(usersResponse);
        return new ResponseEntity<>(a, HttpStatus.OK);
    }
}