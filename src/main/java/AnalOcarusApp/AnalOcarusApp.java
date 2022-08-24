package AnalOcarusApp;

import AnalOcarusApp.Configurations.MongoConfig;
import AnalOcarusApp.Configurations.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ SecurityConfig.class, MongoConfig.class })
public class AnalOcarusApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(AnalOcarusApp.class, args);
    }
}
