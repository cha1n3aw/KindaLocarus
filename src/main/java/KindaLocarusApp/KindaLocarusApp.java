package KindaLocarusApp;

import KindaLocarusApp.Configurations.MongoConfig;
import KindaLocarusApp.Configurations.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static KindaLocarusApp.Constants.Constants.DB_ADDRESS;
import static KindaLocarusApp.Constants.Constants.MONGODB_HOSTNAME;

@SpringBootApplication
@Import({ SecurityConfig.class, MongoConfig.class })
public class KindaLocarusApp
{
    public static void main(String[] args)
    {
        try
        {
            DB_ADDRESS = InetAddress.getByName(MONGODB_HOSTNAME).getHostAddress();
        }
        catch(Exception e)
        {

        }
        SpringApplication.run(KindaLocarusApp.class, args);
    }
}
