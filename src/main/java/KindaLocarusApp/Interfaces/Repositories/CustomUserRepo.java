package KindaLocarusApp.Interfaces.Repositories.Users;

import KindaLocarusApp.Interfaces.Services.Users.Models.CustomUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepo extends MongoRepository<CustomUser, String>
{

}