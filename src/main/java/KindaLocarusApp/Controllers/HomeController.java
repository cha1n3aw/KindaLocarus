package KindaLocarusApp.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController
{
    @ResponseBody
    @GetMapping("/")
    public String HomePage()
    {
        return "In order to access this API, please refer to public documentation first!";
    }
}
