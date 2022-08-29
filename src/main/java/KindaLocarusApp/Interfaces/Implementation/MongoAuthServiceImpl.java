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

import static KindaLocarusApp.Constants.Constants.USERNAME_FIELD;
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
        CustomUser user = mongoTemplate.findOne(Query.query(Criteria.where(USERNAME_FIELD).is(username)), CustomUser.class, USERS_COLLECTION_NAME);
        if (user == null) throw new UsernameNotFoundException("Incorrect username or password!", new Throwable("INCORRECT_CREDENTIALS"));
        else
        {
            HashSet<GrantedAuthority> roles = new HashSet<>((Set<GrantedAuthority>) user.getRoles());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), roles);
        }
    }
}