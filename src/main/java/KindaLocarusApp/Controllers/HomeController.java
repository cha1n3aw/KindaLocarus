package KindaLocarusApp.Controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) return "In order to access this API, please refer to public documentation first! You can <a href=/login>Log In here</a>";
        else return "In order to access this API, please refer to public documentation first! You can <a href=/logout>Log Out here</a>";
    }
}
