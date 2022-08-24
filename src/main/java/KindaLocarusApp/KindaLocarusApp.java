package KindaLocarusApp;

import KindaLocarusApp.Configurations.MongoConfig;
import KindaLocarusApp.Configurations.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ SecurityConfig.class, MongoConfig.class })
public class KindaLocarusApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(KindaLocarusApp.class, args);
    }
}
