package KindaLocarusApp.Interfaces.Services.Users;

import KindaLocarusApp.Interfaces.Services.Users.Models.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomUserService
{
    ResponseEntity<Response<?>> getUsers(final List<String> usernames);
    boolean addUser(final String username, final String password, final List<String> roles);
}
