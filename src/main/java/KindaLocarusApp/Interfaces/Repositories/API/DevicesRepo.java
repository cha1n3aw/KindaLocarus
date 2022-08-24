package KindaLocarusApp.Interfaces.Repositories.API;

import KindaLocarusApp.Models.API.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicesRepo extends MongoRepository<Device, Integer>
{
        //Device findByImei(String imei);
        //List<Device> findAllByImei(List<String> imeies); //parse the result of @finding@ with these params: boolean returnAll, boolean returnActive
        //void deleteByImei(String imei);
        //boolean existsByImei(String imei);
        //List<Device> findByImei(List<String> imeies, boolean returnAll, boolean returnActive);
}
