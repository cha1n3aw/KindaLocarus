package KindaLocarusApp.Interfaces.Repositories.API;

import KindaLocarusApp.Interfaces.Services.Users.Models.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicesRepo extends MongoRepository<Device, Integer>
{

}
