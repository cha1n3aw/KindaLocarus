package KindaLocarusApp.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AccessDeniedController
{
    @GetMapping("/accessDenied")
    @ResponseBody
    public String AccessDenied()
    {
        return "4EL TI V BANE";
    }
}
