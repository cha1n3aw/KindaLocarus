package KindaLocarusApp.Interfaces.Services;

import KindaLocarusApp.Models.CustomUser;
import KindaLocarusApp.Models.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomUserService
{
    Response<?> getUsers(final List<String> usernames, final List<String> fields);
    Response<?> addUsers(final List<CustomUser> customUsers);
    Response<?> deleteUsers(final List<String> usernames);
    Response<?> editUsers(final List<CustomUser> customUsers);
}
