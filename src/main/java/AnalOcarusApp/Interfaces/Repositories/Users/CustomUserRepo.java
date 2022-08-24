package AnalOcarusApp.Interfaces.Repositories.Users;

import AnalOcarusApp.Models.Users.CustomUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepo extends MongoRepository<CustomUser, String>
{
    //ExtendedUser findByUsername(String username);
    CustomUser findExtendedUserById(String Id);
    CustomUser findExtendedUserDetailsByUsername(String username);
    CustomUser findExtendedUserBy(String username);

    //List<ExtendedUserDetails> find(Query query, Class<ExtendedUserDetails> extendedUserDetailsClass);
    //List<ExtendedUser> findAllUsersMatchingIds(List<String> userIds);
    //public boolean existsByUserName (String username);
    //public boolean existsByGrantedAuthorities (String grantedAuthority);
    //ExtendedUser findUserById(String id);
    //List<User> findByUserIds(List<Long> userIds);
    //List<User> findAllById(List<Long> userIds);
    //void deleteById(Long userId);
    //boolean existsById(Long userId);
}