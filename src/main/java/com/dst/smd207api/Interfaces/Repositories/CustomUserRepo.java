package com.dst.smd207api.Interfaces.Repositories;

import com.dst.smd207api.Models.CustomUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomUserRepo extends MongoRepository<CustomUser, String>
{

}