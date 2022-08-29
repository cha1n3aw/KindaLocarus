package KindaLocarusApp.Controllers;

import KindaLocarusApp.Models.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AccessDeniedController
{
    @GetMapping("/accessDenied")
    @ResponseBody
    public ResponseEntity<Response<?>> AccessDenied()
    {
        return new ResponseEntity<>(new Response<>(){{setResponseStatus(HttpStatus.FORBIDDEN.value()); setResponseErrorDesc("Access denied.");}}, HttpStatus.OK);
    }
}
