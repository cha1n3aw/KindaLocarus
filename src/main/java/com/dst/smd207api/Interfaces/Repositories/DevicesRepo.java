package com.dst.smd207api.Interfaces.Repositories;

import com.dst.smd207api.Models.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicesRepo extends MongoRepository<Device, Integer>
{

}
