package com.dst.smd207api.Configurations;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.dst.smd207api.Constants.Constants.*;

@Configuration
public class MongoConfig
{
    @Bean
    public MongoClient mongo()
    {
        ConnectionString connectionString = new ConnectionString(String.format("mongodb://%s:%s/%s", DB_ADDRESS, DB_PORT, DB_NAME));
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception
    {
        return new MongoTemplate(mongo(), DB_NAME);
    }
}