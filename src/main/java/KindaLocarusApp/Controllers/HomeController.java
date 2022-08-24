package KindaLocarusApp.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/")
public class HomeController
{
    @ResponseBody
    @GetMapping
    public String HomePage()
    {
        return "Hello!";
    }
    @ResponseBody
    @GetMapping("/error")
    public String ErrorPage()
    {
        return "Error!!";
    }
}
