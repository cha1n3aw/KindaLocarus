package KindaLocarusApp.Interfaces.Services;

import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface CustomUserService
{
    ResponseEntity<Response<?>> getUsers(final List<String> usernames);
//    boolean addUser(final String username, final String password, final List<String> roles, List<String> ownedDevices, String userDescription);
    boolean addUser(CustomUser customUser);
}
