package KindaLocarusApp.Interfaces.Repositories;

import KindaLocarusApp.Models.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicesRepo extends MongoRepository<Device, Integer>
{

}
