package KindaLocarusApp.Controllers;

import KindaLocarusApp.Models.API.Response;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController
{
    @RequestMapping("/error")
    public ResponseEntity<Response<?>> handleError(HttpServletRequest request)
    {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Response<String> response = new Response<>();
        if (status != null) response.setResponseStatus(Integer.valueOf(status.toString()));
        else response.setResponseStatus(0);
        response.setResponseData("An error occurred while processing your request.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
