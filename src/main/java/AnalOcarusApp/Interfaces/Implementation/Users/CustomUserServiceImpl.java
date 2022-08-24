package AnalOcarusApp.Interfaces.Implementation.Users;

import AnalOcarusApp.Interfaces.Repositories.Users.CustomUserRepo;
import AnalOcarusApp.Interfaces.Services.Users.CustomUserService;
import AnalOcarusApp.Models.Users.CustomUser;
import AnalOcarusApp.Models.API.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static AnalOcarusApp.Constants.Constants.REQUEST_SUCCESS;

@Service
public class CustomUserServiceImpl implements CustomUserService
{
    //private final Authentication authentication;
    private final CustomUserRepo userRepo;

    @Autowired
    public CustomUserServiceImpl(/*Authentication authentication, */CustomUserRepo userRepo)
    {
        //this.authentication = authentication;
        this.userRepo = userRepo;
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
                CustomUser user = userRepo.findExtendedUserDetailsByUsername(username);
                usersResponse += user.getUsername();
                usersResponse += "=";
                for (GrantedAuthority grantedAuthority : userRepo.findExtendedUserDetailsByUsername(authentication.getName()).getAuthorities())
                    usersResponse += (grantedAuthority.getAuthority() + " ");
            }
        }
        a.setResponseData(usersResponse);
        return new ResponseEntity<>(a, HttpStatus.OK);
    }
}