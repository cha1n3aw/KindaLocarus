package com.dst.smd207api.Interfaces.Implementation;

import com.dst.smd207api.Interfaces.Services.SecurityService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService
{
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public SecurityServiceImpl(AuthenticationManager authenticationManager, UserDetailsService userDetailsService)
    {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Boolean login(String username, String password)
    {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated())
        {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            return true;
        }
        else return false;
    }
}
