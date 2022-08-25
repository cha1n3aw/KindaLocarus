package KindaLocarusApp.Interfaces.Implementation;

import KindaLocarusApp.Models.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static KindaLocarusApp.Constants.Constants.USERS_COLLECTION_NAME;

@Service
public class MongoAuthServiceImpl implements UserDetailsService
{
    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoAuthServiceImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        CustomUser user = mongoTemplate.findOne(query, CustomUser.class, USERS_COLLECTION_NAME);
        if (user == null) throw new UsernameNotFoundException("Incorrect username or password!");
        else
        {
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            grantedAuthorities.addAll(user.getAuthorities());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
        }
    }
}