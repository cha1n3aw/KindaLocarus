package KindaLocarusApp.Interfaces.Repositories;

import KindaLocarusApp.Models.CustomUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepo extends MongoRepository<CustomUser, String>
{

}