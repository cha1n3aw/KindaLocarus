package com.dst.smd207api;

import com.dst.smd207api.Configurations.MongoConfig;
import com.dst.smd207api.Configurations.SecurityConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.net.InetAddress;

import static com.dst.smd207api.Constants.Constants.DB_ADDRESS;
import static com.dst.smd207api.Constants.Constants.MONGODB_HOSTNAME;

@Log4j2
@SpringBootApplication
@Import({ SecurityConfig.class, MongoConfig.class })
public class SMD207Application
{
    public static void main(String[] args)
    {
        try
        {
            String mongoAddress = InetAddress.getByName(MONGODB_HOSTNAME).getHostAddress();
            if (!DB_ADDRESS.equals(mongoAddress))
            {
                mongoAddress = DB_ADDRESS;
                log.info(String.format("Found an explicit MongoDb address (via hostname '%s') : %s", MONGODB_HOSTNAME, mongoAddress));
            }
        }
        catch(Exception e)
        {
            log.info("An error occurred while trying to fetch MongoDb IP-address");
        }
        SpringApplication.run(SMD207Application.class, args);
    }
}
