package KindaLocarusApp.Interfaces.Implementation.Users;

import KindaLocarusApp.Interfaces.Repositories.Users.CustomUserRepo;
import KindaLocarusApp.Models.Users.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MongoAuthServiceImpl implements UserDetailsService
{
    private final CustomUserRepo userRepository;

    @Autowired
    public MongoAuthServiceImpl(CustomUserRepo userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        CustomUser user = userRepository.findExtendedUserDetailsByUsername(username);
        if (user == null) throw new UsernameNotFoundException("Incorrect username or password!");
        else
        {
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            grantedAuthorities.addAll(user.getAuthorities());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
        }
    }
}