package KindaLocarusApp.Interfaces.Services.Users;

import KindaLocarusApp.Models.API.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomUserService
{
    ResponseEntity<Response<?>> getUsers(final List<String> usernames);
}
